package com.log.service.handler.cd;

import com.log.config.LogFileProperties;
import com.log.service.LogFileService;
import com.log.service.bean.LogFileAttribute;
import com.log.service.handler.BasicRequestHandler;
import com.log.service.handler.CommonRequest;
import com.log.socket.constants.Respond;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import com.log.subscribe.Subscriber;
import com.log.subscribe.SubscriberManager;
import com.log.util.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * The handler of change directory handler from client.
 * Will respond files under the directory.
 */
@Component
public class ChangeDirectoryHandler extends BasicRequestHandler {
    private final LogFileService logFileService;
    private final static Logger logger = LoggerFactory.getLogger(ChangeDirectoryHandler.class);
    private final SubscriberManager subscriberManager;

    @Autowired
    public ChangeDirectoryHandler(LogFileService logFileService, SubscriberManager subscriberManager) {
        this.logFileService = logFileService;
        this.subscriberManager = subscriberManager;
    }

    @Override
    protected void handle(ChannelHandlerContext ctx, LogP msg, CommonRequest request) throws Exception {
        logger.debug("{} change directory to {}", ctx.channel().remoteAddress(), request.getPath());
        //if path is null. return the root
        List<LogFileAttribute> result;
        if (Strings.isBlank(request.getPath())) {
            result = logFileService.listRoot();
        } else {
            if (!FileUtils.checkValid(request.getPath())) {
                throw new IllegalAccessException(request.getPath() + " is out of valid access range");
            }
            result = this.logFileService.listLogFiles(request.getPath(), false);
            subscriberManager.remove(ctx, DirectoryFileFilter.INSTANCE);
            Subscriber subscriber = new Subscriber(new File(request.getPath()), ctx);
            subscriber.setDeleteHandler(s -> send(logFileService.listRoot(), ctx, null));
            subscriber.setModifyHandler(s -> {
                List<LogFileAttribute> data = this.logFileService.listLogFiles(request.getPath(), false);
                send(data, ctx, request.getPath());
            });
            subscriberManager.subscribe(subscriber);
        }
        send(result, ctx, request.getPath());
    }

    private void send(List<LogFileAttribute> data, ChannelHandlerContext ctx, String requestPath) {
        Collections.sort(data);
        final Path parent = Strings.isBlank(requestPath) ? null : Paths.get(requestPath).getParent();
        LogPFactory respond = LogPFactory.defaultInstance0()
                .setRespond(Respond.LIST_FILE)
                .addData("dir", requestPath)
                .addData("files", data);
        if (parent != null && FileUtils.checkValid(parent.toString())) {
            respond.addData("rollback", parent.toString());
        }
        ctx.writeAndFlush(respond.create()).addListener(future -> {
            if (future.isSuccess()) {
                logger.debug("send successfully");
            } else {
                throw new RuntimeException("writeAndFlush error");
            }
        });
    }
}
