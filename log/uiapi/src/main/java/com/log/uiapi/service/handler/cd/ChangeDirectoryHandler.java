package com.log.uiapi.service.handler.cd;

import com.log.uiapi.protocol.constants.Respond;
import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.protocol.logp.LogPFactory;
import com.log.uiapi.service.FileService;
import com.log.uiapi.service.bean.LogFileAttribute;
import com.log.uiapi.service.bean.RollbackAttribute;
import com.log.uiapi.service.handler.BasicAuthRequestHandler;
import com.log.uiapi.service.handler.PathRequest;
import com.log.uiapi.subscribe.LinkedSubscribe;
import com.log.uiapi.subscribe.Subscriber;
import com.log.uiapi.subscribe.SubscriberManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * The handler of change directory handler from client.
 * Will respond files under the directory.
 */
@Component
public class ChangeDirectoryHandler extends BasicAuthRequestHandler<PathRequest> {
    private final FileService fileService;
    private final static Logger logger = LoggerFactory.getLogger(ChangeDirectoryHandler.class);
    private final SubscriberManager subscriberManager;

    @Autowired
    public ChangeDirectoryHandler(FileService fileService, SubscriberManager subscriberManager) {
        super(PathRequest.class);
        this.fileService = fileService;
        this.subscriberManager = subscriberManager;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, PathRequest request) throws Exception {
        logger.debug("{} change directory to {}", ctx.channel().remoteAddress(), request.getPath());
        //if path is null. return the root
        List<LogFileAttribute> result;
        if (Strings.isBlank(request.getPath())) {
            result = fileService.list(request.getHostName(), null);
        } else {
            result = this.fileService.list(request.getHostName(), request.getPath());
            subscriberManager.remove(ctx, DirectoryFileFilter.INSTANCE);
            Subscriber subscriber = new LinkedSubscribe(request.getHostName(), new File(request.getPath()), ctx);
            subscriber.setDeleteHandler(() -> {
                send(fileService.list(request.getHostName(), null), ctx, request);
                logger.debug("{} delete, send empty dir files info to {}", request.getPath(), ctx.channel().remoteAddress());
            });
            subscriber.setModifyHandler(() -> {
                logger.debug("{} modify, send latest dir files info to {}", request.getPath(), ctx.channel().remoteAddress());
                List<LogFileAttribute> data = this.fileService.list(request.getHostName(), request.getPath());
                send(data, ctx, request);
            });
            subscriberManager.subscribe(subscriber);
        }
        send(result, ctx, request);
    }

    private void send(List<LogFileAttribute> data, ChannelHandlerContext ctx, PathRequest request) {
        Collections.sort(data);
        RollbackAttribute rollbackAttribute;
        if (Strings.isBlank(request.getPath())) {
            rollbackAttribute = new RollbackAttribute();
            rollbackAttribute.setInHostPath(true);
        } else {
            rollbackAttribute = this.fileService.directoryContext(request.getHostName(), request.getPath());
        }

        LogPFactory respond = LogPFactory.defaultInstance0()
                .setRespond(Respond.LIST_FILE)
                .addData("dir", request.getPath())
                .addData("files", data)
                .addData("rollback", rollbackAttribute);
        ctx.writeAndFlush(respond.create());
    }
}
