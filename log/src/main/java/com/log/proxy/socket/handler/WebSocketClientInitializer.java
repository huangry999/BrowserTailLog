//package com.log.proxy.socket.handler;
//
//import com.log.common.spring.SpringUtils;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.http.DefaultHttpHeaders;
//import io.netty.handler.codec.http.HttpClientCodec;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
//import io.netty.handler.codec.http.websocketx.WebSocketVersion;
//import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
//
//import java.net.URI;
//
//public class WebSocketClientInitializer extends ChannelInitializer<SocketChannel> {
//
//    private final String host;
//    private final Channel inboundChannel;
//
//    public WebSocketClientInitializer(String host, Channel inboundChannel) {
//        this.host = host;
//        this.inboundChannel = inboundChannel;
//    }
//
//    @Override
//    public void initChannel(SocketChannel ch) throws Exception {
//        ChannelPipeline pipeline = ch.pipeline();
//        int remotePort = SpringUtils.getProperty("netty.port", Integer.class);
//        URI uri = new URI(String.format("ws://%s:%s/%s", host, remotePort, LogWebSocketInitializer.WEBSOCKET_PATH));
//        final WebSocketClientHandler handler =
//                new WebSocketClientHandler(
//                        WebSocketClientHandshakerFactory.newHandshaker(
//                                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()),
//                        inboundChannel);
//        pipeline.addLast(
//                new HttpClientCodec(),
//                new HttpObjectAggregator(SpringUtils.getProperty("netty.httpBlockMaxByte", Integer.class)),
//                WebSocketClientCompressionHandler.INSTANCE,
//                handler);
//    }
//}
