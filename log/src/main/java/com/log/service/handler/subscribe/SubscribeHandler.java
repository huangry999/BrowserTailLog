package com.log.service.handler.subscribe;

import com.log.logreader.LogReader;
import com.log.service.bean.LogLineText;
import com.log.service.handler.BasicAuthRequestHandler;
import com.log.service.handler.PathRequest;
import com.log.socket.constants.Mode;
import com.log.socket.constants.Respond;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import com.log.subscribe.LinkedSubscribe;
import com.log.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class SubscribeHandler extends BasicAuthRequestHandler<PathRequest> {

    private final static Logger logger = LoggerFactory.getLogger(SubscribeHandler.class);
    private final SubscriberManager subscriberManager;
    @Value("${log.windowSize}")
    private int windowSize;
    private final LogReader logReader;

    @Autowired
    public SubscribeHandler(SubscriberManager subscriberManager, LogReader logReader) {
        super(PathRequest.class);
        this.subscriberManager = subscriberManager;
        this.logReader = logReader;
    }

    @Override
    protected void onClose(ChannelHandlerContext ctx, Future future) {
        logger.debug("{} close and remove all subscribers", ctx.channel().remoteAddress());
        this.subscriberManager.remove(ctx);
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, PathRequest request) throws Exception {
        File file = new File(request.getPath());
        logger.debug("Create new subscribe {} to log {}", ctx.channel().remoteAddress(), request.getPath());
        LinkedSubscribe subscriber = new LinkedSubscribe(file, ctx);
        subscriber.setModifyHandler(s -> {
            try {
                subscriber.getLock().lock();
                List<LogLineText> contents = logReader.read(s.getFile(), subscriber.getReadIndex(), Integer.MAX_VALUE);
                logger.debug("{} change, will send content lines skip {} and take {}, dst: {}",
                        file.getAbsolutePath(),
                        subscriber.getReadIndex(),
                        contents.size(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .addData("data", contents)
                        .addData("path", s.getFile().getPath())
                        .setMode(Mode.MODIFY)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .create();
                ctx.writeAndFlush(logP).sync().addListener(future -> {
                    if (future.isSuccess()) {
                        subscriber.setReadIndex(subscriber.getReadIndex() + contents.size());
                        logger.debug("send success, the subscribe read index add to {}", subscriber.getReadIndex());
                    } else {
                        throw new RuntimeException("writeAndFlush error");
                    }
                });
            } catch (Exception e) {
                logger.error(
                        "log {} modified and send frame to {} error: ",
                        s.getFile().getAbsolutePath(),
                        ctx.channel().remoteAddress(),
                        e);
            } finally {
                subscriber.getLock().unlock();
            }
        });
        subscriber.setDeleteHandler(s -> {
            try {
                subscriber.getLock().lock();
                subscriber.setReadIndex(0);
                logger.debug("{} delete, set subscribe read index to {}, dst: {}",
                        file.getAbsolutePath(),
                        subscriber.getReadIndex(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .setMode(Mode.DELETE)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .addData("path", s.getFile().getPath())
                        .create();
                ctx.writeAndFlush(logP).sync().addListener(future -> {
                    if (future.isSuccess()) {
                        logger.debug("send success");
                    } else {
                        throw new RuntimeException("writeAndFlush error ");
                    }
                });
            } catch (Exception e) {
                logger.error(
                        "log {} delete and send frame to {} error: ",
                        s.getFile().getAbsolutePath(),
                        ctx.channel().remoteAddress(),
                        e);
            } finally {
                subscriber.getLock().unlock();
            }
        });
        subscriber.setCreateHandler(s -> {
            try {
                subscriber.getLock().lock();
                long lastLineIndex = logReader.count(s.getFile());
                long skip = lastLineIndex <= windowSize ? 0 : lastLineIndex - windowSize;
                List<LogLineText> contents = logReader.read(s.getFile(), skip, windowSize);
                logger.debug("{} create, will send content lines skip {} and take {}, dst: {}",
                        file.getAbsolutePath(),
                        skip,
                        contents.size(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .addData("data", contents)
                        .addData("path", s.getFile().getPath())
                        .setMode(Mode.CREATE)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .create();
                ctx.writeAndFlush(logP).sync().addListener(future -> {
                    if (future.isSuccess()) {
                        subscriber.setReadIndex(lastLineIndex);
                        logger.debug("send success, the subscribe read index add to {}", subscriber.getReadIndex());
                    } else {
                        throw new RuntimeException("writeAndFlush error ");
                    }
                });
            } catch (Exception e) {
                logger.error(
                        "log {} create and send frame to {} error: ",
                        s.getFile().getAbsolutePath(),
                        ctx.channel().remoteAddress(),
                        e);
            } finally {
                subscriber.getLock().unlock();
            }
        });
        //send init context
        try {
            subscriber.getLock().lock();
            long lastLineIndex = logReader.count(file);
            long skip = lastLineIndex <= windowSize ? 0 : lastLineIndex - windowSize;
            List<LogLineText> contents = logReader.read(file, skip, windowSize);
            logger.debug("{} init, will send content lines skip {} and take {}, dst: {}",
                    file.getAbsolutePath(),
                    skip,
                    contents.size(),
                    ctx.channel().remoteAddress());
            LogP logP = LogPFactory.defaultInstance0()
                    .addData("data", contents)
                    .addData("path", file.getPath())
                    .setRespond(Respond.LOG_CONTENT_BETWEEN)
                    .create();
            ctx.writeAndFlush(logP).sync().addListener(future -> {
                if (future.isSuccess()) {
                    subscriber.setReadIndex(skip + contents.size());
                    logger.debug("send success, the subscribe read index add to {}", subscriber.getReadIndex());
                } else {
                    throw new RuntimeException("writeAndFlush error " + future.cause());
                }
            });
        } catch (Exception e) {
            logger.error(
                    "log {} init and send frame to {} error: ",
                    file.getAbsolutePath(),
                    ctx.channel().remoteAddress(),
                    e);
        } finally {
            subscriber.getLock().unlock();
        }
        subscriberManager.subscribe(subscriber);
    }


}
