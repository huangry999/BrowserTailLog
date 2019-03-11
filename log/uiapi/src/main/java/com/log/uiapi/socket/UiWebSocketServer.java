package com.log.uiapi.socket;

import com.log.common.spring.SpringUtils;
import com.log.uiapi.protocol.codec.LogProtocolCodec;
import com.log.uiapi.socket.handler.BusinessHandler;
import com.log.uiapi.socket.handler.ExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service
public class UiWebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(UiWebSocketServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    /**
     * start netty service
     *
     * @param address bind address
     * @throws Exception exception
     */
    public void start(InetSocketAddress address) throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new UiWebSocketInitializer());
        bootstrap.bind(address).addListener((ChannelFutureListener) future -> channel = future.channel());
    }

    /**
     * shutdown listening
     */
    public void destroy() {
        logger.info("Shutdown log service");
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}

class UiWebSocketInitializer extends ChannelInitializer<SocketChannel> {

    private static final String WEBSOCKET_PATH = "/log";

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (SpringUtils.getProperty("netty.ssl", Boolean.class)) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            SslContext cxt = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            pipeline.addLast(cxt.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(SpringUtils.getProperty("netty.httpBlockMaxByte", Integer.class)));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new LogProtocolCodec());
        pipeline.addLast(new BusinessHandler());
        pipeline.addLast(new ExceptionHandler());
    }
}
