package com.log.socket.codec;

import com.log.socket.constants.Mode;
import com.log.socket.constants.Request;
import com.log.socket.constants.Sender;
import com.log.socket.logp.LogP;
import com.log.socket.logp.head.*;
import com.log.util.Conversion;
import com.log.util.LogProtocolUtils;
import com.log.util.PrintUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.lang3.Validate;

import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.util.List;

import static com.log.socket.logp.head.FrameHead.SIZE;

public class LogProtocolCodec extends MessageToMessageCodec<BinaryWebSocketFrame, LogP> {
    private int lengthFieldEndOffset;
    public static final Charset CHARSET = Charset.forName("utf-8");

    public LogProtocolCodec() {
        lengthFieldEndOffset = StartFlag.SIZE + Size.SIZE;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogP msg, List<Object> out) throws Exception {
        byte[] head = encodeHead(msg.getHead());
        BinaryWebSocketFrame respond = new BinaryWebSocketFrame();
        respond.content().writeBytes(head);
        respond.content().writeCharSequence(msg.getBody(), CHARSET);
        out.add(respond);
    }

    /**
     * encode the head by FrameHead, as well as calculate the checksum.
     *
     * @param head head bean
     * @return byte array
     */
    public byte[] encodeHead(@NotNull FrameHead head) {
        byte[] result = new byte[FrameHead.SIZE];
        int start = 0;

        //encode start flag
        Conversion.shortToByteArray(head.getStartFlag().getValue(), 0, result, start, StartFlag.SIZE);
        start += StartFlag.SIZE;

        //encode size
        Conversion.shortToByteArray(head.getSize().getValue(), 0, result, start, Size.SIZE);
        start += Size.SIZE;

        //encode version
        boolean[] versionBinary = new boolean[Version.SIZE * 8];
        short mv = head.getVersion().getMainVersion();
        short sv = head.getVersion().getSubVersion();
        Conversion.shortToBinary(mv, 12, versionBinary, 0, 4);
        Conversion.shortToBinary(sv, 12, versionBinary, 4, 4);
        result[start] = Conversion.binaryToByte(versionBinary, 0, (byte) 0, 8);
        start += Version.SIZE;

        //encode level
        boolean[] levelBinary = new boolean[Level.SIZE * 8];
        short tv = head.getLevel().getTotal();
        short cv = head.getLevel().getCurrent();
        Conversion.shortToBinary(tv, 12, levelBinary, 0, 4);
        Conversion.shortToBinary(cv, 12, levelBinary, 4, 4);
        result[start] = Conversion.binaryToByte(levelBinary, 0, (byte) 0, 8);
        start += Level.SIZE;

        //encode control signal
        boolean[] controlBinary = new boolean[ControlSignal.SIZE * 8];
        controlBinary[0] = head.getControlSignal().getSender().getFlag();
        Conversion.intToBinary(head.getControlSignal().getRequest().getFlag(), 25, controlBinary, 1, 7);
        result[start] = Conversion.binaryToByte(controlBinary, 0, (byte) 0, 8);
        start += ControlSignal.SIZE;

        //encode mode
        result[start] = Conversion.intToByteArray(head.getMode().getFlag(), 24, new byte[1], 0, Mode.SIZE)[0];
        start += Mode.SIZE;

        int checksum = LogProtocolUtils.calculateChecksum(result);
        Conversion.intToByteArray(checksum, 16, result, start, Checksum.SIZE);
        start += Checksum.SIZE;
        Validate.isTrue(start == FrameHead.SIZE);
        return result;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf in = msg.content();
        if (in.readableBytes() < lengthFieldEndOffset) {
            return;
        }
        int frameLength = in.getUnsignedShort(2);
        if (frameLength < 0) {
            failOnNegativeLengthField(in, frameLength, lengthFieldEndOffset);
        }
        if (frameLength < lengthFieldEndOffset) {
            failOnFrameLengthLessThanLengthFieldEndOffset(in, frameLength, lengthFieldEndOffset);
        }
        if (in.readableBytes() < frameLength) {
            return;
        }
        byte[] headByte = new byte[SIZE];
        in.readBytes(headByte);
        LogP logP = new LogP();
        FrameHead head = this.decodeHead(headByte);
        logP.setHead(head);
        String body = in.readCharSequence(head.getSize().getValue() - SIZE, CHARSET).toString();
        logP.setBody(body);
        out.add(logP);
    }

    private static void failOnFrameLengthLessThanLengthFieldEndOffset(ByteBuf in,
                                                                      long frameLength,
                                                                      int lengthFieldEndOffset) {
        in.skipBytes(lengthFieldEndOffset);
        throw new CorruptedFrameException(
                "Adjusted frame length (" + frameLength + ") is less " +
                        "than lengthFieldEndOffset: " + lengthFieldEndOffset);
    }

    private static void failOnNegativeLengthField(ByteBuf in, long frameLength, int lengthFieldEndOffset) {
        in.skipBytes(lengthFieldEndOffset);
        throw new CorruptedFrameException(
                "negative pre-adjustment length field: " + frameLength);
    }

    /**
     * decode the head.
     *
     * @param headBytes head byte array.
     * @return head bean
     */
    public FrameHead decodeHead(byte[] headBytes) {
        Validate.isTrue(headBytes.length == SIZE);
        FrameHead head = new FrameHead();
        int start = 0;

        //validate checksum
        if (LogProtocolUtils.calculateChecksum(headBytes) != 0) {
            throw new codecException("checksum error: " + PrintUtils.toString(headBytes));
        }

        //parse start flag
        short sfv = Conversion.byteArrayToShort(headBytes, start, (short) 0, StartFlag.SIZE);
        StartFlag startFlag = new StartFlag(sfv);
        head.setStartFlag(startFlag);
        start += StartFlag.SIZE;

        //parse size
        short sizeVal = Conversion.byteArrayToShort(headBytes, start, (short) 0, Size.SIZE);
        Size size = new Size(sizeVal);
        start += Size.SIZE;
        head.setSize(size);

        //parse version
        boolean[] versionBit = new boolean[8];
        Conversion.byteToBinary(headBytes[start], 0, versionBit, 0, Version.SIZE * 8);
        start += Version.SIZE;
        Version version = new Version();
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

        //parse checksum
        short cv = Conversion.byteArrayToShort(headBytes, start, (short) 0, Checksum.SIZE);
        Checksum checksum = new Checksum(cv);
        start += Checksum.SIZE;
        head.setChecksum(checksum);

        Validate.isTrue(start == SIZE);

        return head;
    }
}
