package com.log.uiapi.service.handler.subscribe;

import com.log.uiapi.protocol.constants.Mode;
import com.log.uiapi.protocol.constants.Respond;
import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.protocol.logp.LogPFactory;
import com.log.uiapi.service.FileService;
import com.log.uiapi.service.bean.LogLineText;
import com.log.uiapi.service.handler.BasicAuthRequestHandler;
import com.log.uiapi.service.handler.PathRequest;
import com.log.uiapi.subscribe.LinkedSubscribe;
import com.log.uiapi.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class SubscribeHandler extends BasicAuthRequestHandler<PathRequest> {

    @Value("${uiapi-properties.window-size}")
    private int windowSize;
    private final SubscriberManager subscriberManager;
    private final FileService fileService;

    @Autowired
    public SubscribeHandler(SubscriberManager subscriberManager, FileService fileService) {
        super(PathRequest.class);
        this.fileService = fileService;
        this.subscriberManager = subscriberManager;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, PathRequest request) {
        File file = new File(request.getPath());
        log.debug("Create new subscribe {} to log {}", ctx.channel().remoteAddress(), request.getPath());
        LinkedSubscribe subscriber = new LinkedSubscribe(request.getHostName(), file, ctx);
        subscriber.setModifyHandler(() -> {
            try {
                subscriber.getLock().lock();
                List<LogLineText> contents = fileService.read(request.getHostName(), request.getPath(), subscriber.getReadIndex(), Integer.MAX_VALUE);
                log.debug("{} change, will send content lines skip {} and take {}, dst: {}",
                        file.getAbsolutePath(),
                        subscriber.getReadIndex(),
                        contents.size(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .addData("data", contents)
                        .addData("path", request.getPath())
                        .setMode(Mode.MODIFY)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .create();
                ctx.writeAndFlush(logP).sync().addListener(future -> {
                    if (future.isSuccess()) {
                        subscriber.setReadIndex(subscriber.getReadIndex() + contents.size());
                    }
                });
            } catch (Exception e) {
                log.error(
                        "log {} modified and send frame to {} error: ",
                        request.getPath(),
                        ctx.channel().remoteAddress(),
                        e);
            } finally {
                subscriber.getLock().unlock();
            }
        });
        subscriber.setDeleteHandler(() -> {
            try {
                subscriber.getLock().lock();
                subscriber.setReadIndex(0);
                log.debug("{} delete, remove the subscriber to {}",
                        file.getAbsolutePath(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .setMode(Mode.DELETE)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .addData("path", request.getPath())
                        .create();
                ctx.writeAndFlush(logP).sync();
            } catch (Exception e) {
                log.error(
                        "log {} delete and send frame to {} error: ",
                        request.getPath(),
                        ctx.channel().remoteAddress(),
                        e);
            } finally {
                subscriber.getLock().unlock();
            }
        });
        subscriber.setCreateHandler(() -> {
            try {
                subscriber.getLock().lock();
                long lastLineIndex = fileService.totalLineNo(request.getHostName(), request.getPath());
                long skip = lastLineIndex <= windowSize ? 0 : lastLineIndex - windowSize;
                List<LogLineText> contents = fileService.read(request.getHostName(), request.getPath(), skip, windowSize);
                log.debug("{} create, will send content lines skip {} and take {}, dst: {}",
                        file.getAbsolutePath(),
                        skip,
                        contents.size(),
                        ctx.channel().remoteAddress());
                LogP logP = LogPFactory.defaultInstance0()
                        .addData("data", contents)
                        .addData("path", request.getPath())
                        .setMode(Mode.CREATE)
                        .setRespond(Respond.NEW_LOG_CONTENT)
                        .create();
                ctx.writeAndFlush(logP).sync().addListener(future -> {
                    if (future.isSuccess()) {
                        subscriber.setReadIndex(lastLineIndex);
                    }
                });
            } catch (Exception e) {
                log.error(
                        "log {} create and send frame to {} error: ",
                        request.getPath(),
                        ctx.channel().remoteAddress(),
                        e);
            } finally {
                subscriber.getLock().unlock();
            }
        });
        //send init context
        try {
            subscriber.getLock().lock();
            long lastLineIndex = fileService.totalLineNo(request.getHostName(), file.getPath());
            long skip = lastLineIndex <= windowSize ? 0 : lastLineIndex - windowSize;
            List<LogLineText> contents = fileService.read(request.getHostName(), file.getPath(), skip, windowSize);
            log.debug("{} init, will send content lines skip {} and take {}, dst: {}",
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
                    subscriber.setReadIndex(lastLineIndex);
                }
            });
        } catch (Exception e) {
            log.error(
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
