/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.log.socket;

import com.log.socket.codec.LogProtocolCodec;
import com.log.socket.handler.LogRequestHandler;
import com.log.util.SpringUtils;
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

    public static final String WEBSOCKET_PATH = "/log";

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (Boolean.valueOf(SpringUtils.context().getEnvironment().getProperty("${netty.ssl}"))) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            SslContext cxt = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            pipeline.addLast(cxt.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.valueOf(
                SpringUtils.context().getEnvironment().getProperty("${netty.httpBlockMaxByte}", "1024"))));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new LogProtocolCodec());
        pipeline.addLast(new LogRequestHandler());
    }
}
