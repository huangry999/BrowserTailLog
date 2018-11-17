package com.log.socket.decode;

import com.log.socket.logp.LogP;
import com.log.socket.logp.constants.Mode;
import com.log.socket.logp.constants.Request;
import com.log.socket.logp.constants.Sender;
import com.log.socket.logp.head.*;
import com.log.util.Conversion;
import com.log.util.LogProtocolUtils;
import com.log.util.PrintUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.commons.lang3.Validate;

import java.util.List;

import static com.log.socket.logp.head.FrameHead.SIZE;

public class LogProtocolDecode extends MessageToMessageDecoder<WebSocketFrame> {

    public FrameHead decodeHead(byte[] headBytes) {
        Validate.isTrue(headBytes.length == SIZE);
        FrameHead head = new FrameHead();
        int start = 0;

        //validate checksum
        if (LogProtocolUtils.calculateChecksum(headBytes) != 0) {
            throw new DecodeException("checksum error: " + PrintUtils.toString(headBytes));
        }

        //parse start flag
        short sfv = Conversion.byteArrayToShort(headBytes, start, (short) 0, StartFlag.SIZE);
        StartFlag startFlag = new StartFlag(sfv);
        head.setStartFlag(startFlag);
        start += StartFlag.SIZE;

        //parse version
        boolean[] versionBit = new boolean[8];
        Conversion.byteToBinary(headBytes[start], 0, versionBit, 0, ProtocolVersion.SIZE * 8);
        start += ProtocolVersion.SIZE;
        ProtocolVersion version = new ProtocolVersion();
        version.setMainVersion(Conversion.binaryToShort(versionBit, 0, (short) 0, 4));
        version.setSubVersion(Conversion.binaryToShort(versionBit, 4, (short) 0, 4));
        head.setVersion(version);

        //parse data level
        boolean[] levelBit = new boolean[8];
        Conversion.byteToBinary(headBytes[start], 0, levelBit, 0, Level.SIZE * 8);
        start += Level.SIZE;
        Level level = new Level();
        level.setTotal(Conversion.binaryToShort(levelBit, 0, (short) 0, 4));
        level.setCurrent(Conversion.binaryToShort(levelBit, 4, (short) 0, 4));
        head.setLevel(level);

        //parse control signal
        boolean[] controlBit = new boolean[8];
        Conversion.byteToBinary(headBytes[start], 0, controlBit, 0, ControlSignal.SIZE * 8);
        start += ControlSignal.SIZE;
        ControlSignal controlSignal = new ControlSignal();
        controlSignal.setSender(Sender.valueOf(controlBit[0]));
        int requestFlag = Conversion.binaryToInt(controlBit, 1, 0, 7);
        controlSignal.setRequest(Request.valueOf(requestFlag));
        head.setControlSignal(controlSignal);

        //parse mode
        head.setMode(Mode.valueOf(headBytes[start]));
        start += Mode.SIZE;

        //parse data package size
        short sizeVal = Conversion.byteArrayToShort(headBytes, start, (short) 0, DataPackageSize.SIZE);
        DataPackageSize size = new DataPackageSize(sizeVal);
        start += DataPackageSize.SIZE;
        head.setDataPackageSize(size);

        //parse checksum
        short cv = Conversion.byteArrayToShort(headBytes, start, (short) 0, Checksum.SIZE);
        Checksum checksum = new Checksum(cv);
        start += Checksum.SIZE;
        head.setChecksum(checksum);

        Validate.isTrue(start == SIZE);

        return head;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        if (!(msg instanceof BinaryWebSocketFrame)) {
            ctx.writeAndFlush("Only support binary frame");
            return;
        }
        if (msg.content().readableBytes() < SIZE) {
            return;
        }
        ByteBuf headBuf = msg.content().readRetainedSlice(SIZE);
        byte[] headByt = new byte[SIZE];
        headBuf.getBytes(0, headByt);
        LogP logP = new LogP();
        FrameHead head = decodeHead(headByt);
        logP.setHead(head);
        out.add(logP);
    }
}
