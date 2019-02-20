package com.lookforlog.log.socket;

import com.lookforlog.protocol.codec.LogProtocolCodec;
import com.lookforlog.log.socket.handler.BusinessHandler;
import com.lookforlog.log.socket.handler.ExceptionHandler;
import com.lookforlog.util.SpringUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class LogWebSocketInitializer extends ChannelInitializer<SocketChannel> {

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
