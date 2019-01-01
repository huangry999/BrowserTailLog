package com.log;

import com.log.socket.codec.LogProtocolCodec;
import com.log.socket.constants.Request;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import com.log.util.PrintUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.log.socket.codec.LogProtocolCodec.CHARSET;

public class GenerateBinaryFrame {
    public static void main(String[] args) throws Exception {
        LogP logP = LogPFactory.defaultInstance0()
                .setRequest(Request.INIT)
                .create();

        LogProtocolCodec codec = new LogProtocolCodec();
        byte[] head = codec.encodeHead(logP.getHead());
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(head);
        buf.writeCharSequence(logP.getBody(), CHARSET);
        byte[] frame = new byte[buf.readableBytes()];
        buf.readBytes(frame);
        System.out.println(PrintUtils.toString0(frame, " "));
        System.out.println(PrintUtils.toString(frame));
    }
}
