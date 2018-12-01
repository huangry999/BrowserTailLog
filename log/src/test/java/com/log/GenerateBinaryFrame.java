package com.log;

import com.log.socket.codec.LogProtocolCodec;
import com.log.socket.constants.Request;
import com.log.socket.constants.Sender;
import com.log.socket.logp.LogP;
import com.log.socket.logp.LogPFactory;
import com.log.socket.logp.head.ControlSignal;
import com.log.util.PrintUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.log.socket.codec.LogProtocolCodec.CHARSET;

public class GenerateBinaryFrame {
    public static void main(String[] args) throws Exception {
        ControlSignal signal = new ControlSignal();
        signal.setRequest(Request.REQUEST_BETWEEN);
        signal.setSender(Sender.CLIENT);
        LogP logP = LogPFactory.defaultInstance0()
                .setControlSignal(signal)
                .addData("path", "G:\\log\\2018-12-13\\acq.log")
                .addData("from", 1)
                .addData("to", 3)
                .create();

        LogProtocolCodec codec = new LogProtocolCodec();
        byte[] head = codec.encodeHead(logP.getHead());
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(head);
        buf.writeCharSequence(logP.getBody(), CHARSET);
        byte[] frame = new byte[buf.readableBytes()];
        buf.readBytes(frame);
        System.out.println(PrintUtils.toString0(frame, " "));
    }
}
