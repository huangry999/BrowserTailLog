package com.log.service.handler.subscribe;

import com.log.service.bean.LogLineText;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.codec.LogProtocolCodec;
import com.log.socket.constants.Mode;
import com.log.socket.constants.Respond;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import com.log.subscribe.Subscriber;
import com.log.subscribe.SubscriberManager;
import com.log.util.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Component
public class SubscribeHandler extends BasicRequestHandler {

    private final static Logger logger = LoggerFactory.getLogger(SubscribeHandler.class);
    private final SubscriberManager subscriberManager;

    @Value("${log.windowSize}")
    private long windowSize;

    @Autowired
    public SubscribeHandler(SubscriberManager subscriberManager) {
        this.subscriberManager = subscriberManager;
    }

    @Override
    protected void onClose(ChannelHandlerContext ctx, Future future) {
        this.subscriberManager.remove(ctx);
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception {
        File file = new File(request.getPath());
        logger.debug("Create new subscribe {} to log {}", ctx.channel().remoteAddress(), request.getPath());
        Subscriber subscriber = new Subscriber(file, ctx);
        subscriber.setModifyHandler(s -> {
            try {
                List<LogLineText> contents = FileUtils.getLogText(s.getFile(), s.getReadIndex(), windowSize);
                logger.debug("{} change, will send content lines skip {} and take {}, dst: {}",
                        file.getAbsolutePath(),
                        s.getReadIndex(),
                        contents.size(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .addData("data", contents)
                        .addData("path", s.getFile().getPath())
                        .setMode(Mode.MODIFY)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .create();
                ctx.writeAndFlush(logP).addListener(future -> {
                    if (future.isSuccess()) {
                        s.setReadIndex(s.getReadIndex() + contents.size());
                        logger.debug("send success, the subscribe read index add to {}", s.getReadIndex());
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
            }
        });
        subscriber.setDeleteHandler(s -> {
            s.setReadIndex(0);
            logger.debug("{} delete, set subscribe read index to {}, dst: {}",
                    file.getAbsolutePath(),
                    s.getReadIndex(),
                    ctx.channel().remoteAddress());
            LogP logP = LogPFactory.defaultInstance0()
                    .setMode(Mode.DELETE)
                    .setRespond(Respond.NEW_LOG_CONTENT)
                    .addData("path", s.getFile().getPath())
                    .create();
            ctx.writeAndFlush(logP).addListener(future -> {
                if (future.isSuccess()) {
                    logger.debug("send success");
                } else {
                    throw new RuntimeException("writeAndFlush error ");
                }
            });
        });
        subscriber.setCreateHandler(s -> {
            try {
                long lastLineIndex = Files.lines(s.getFile().toPath(), LogProtocolCodec.CHARSET).count();
                long skip = lastLineIndex <= windowSize ? 0 : lastLineIndex - windowSize;
                List<LogLineText> contents = FileUtils.getLogText(s.getFile(), skip, windowSize);
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
                ctx.writeAndFlush(logP).addListener(future -> {
                    if (future.isSuccess()) {
                        s.setReadIndex(lastLineIndex);
                        logger.debug("send success, the subscribe read index add to {}", s.getReadIndex());
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
            }
        });
        //send init context
        if (file.exists()) {
            try {
                long lastLineIndex = Files.lines(file.toPath(), LogProtocolCodec.CHARSET).count();
                long skip = lastLineIndex <= windowSize ? 0 : lastLineIndex - windowSize;
                List<LogLineText> contents = FileUtils.getLogText(file, skip, windowSize);
                logger.debug("{} init, will send content lines skip {} and take {}, dst: {}",
                        file.getAbsolutePath(),
                        skip,
                        contents.size(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .addData("data", contents)
                        .addData("path", file.getPath())
                        .setMode(Mode.CREATE)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .create();
                ctx.writeAndFlush(logP).addListener(future -> {
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
            }
        }
        subscriberManager.subscribe(subscriber);
    }


}
