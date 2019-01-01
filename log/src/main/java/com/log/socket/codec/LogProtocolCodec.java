package com.log.socket.codec;

import com.log.socket.constants.Mode;
import com.log.socket.constants.Request;
import com.log.socket.constants.Respond;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.util.List;

import static com.log.socket.logp.head.FrameHead.SIZE;

public class LogProtocolCodec extends MessageToMessageCodec<BinaryWebSocketFrame, LogP> {
    private int lengthFieldEndOffset;
    public static final Charset CHARSET = Charset.forName("utf-8");
    private static final Logger logger = LoggerFactory.getLogger(LogProtocolCodec.class);

    public LogProtocolCodec() {
        lengthFieldEndOffset = StartFlag.SIZE + Size.SIZE;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogP msg, List<Object> out) throws Exception {
        byte[] head = encodeHead(msg.getHead());
        BinaryWebSocketFrame respond = new BinaryWebSocketFrame();
        respond.content().writeBytes(head);
        respond.content().writeCharSequence(msg.getBody(), CHARSET);
        logger.debug("encode frame: {}", msg);
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
        if (head.getSize() != null){
            Conversion.shortToByteArray(head.getSize().getValue(), 0, result, start, Size.SIZE);
        }
        start += Size.SIZE;


        //encode version
        boolean[] versionBinary = new boolean[Version.SIZE * 8];
        short mv = head.getVersion().getMainVersion();
        short sv = head.getVersion().getSubVersion();
        Conversion.shortToBinary(mv, 12, versionBinary, 0, 4);
        Conversion.shortToBinary(sv, 12, versionBinary, 4, 4);
        result[start] = Conversion.binaryToByte(versionBinary, 0, (byte) 0, 8);
        start += Version.SIZE;

        //encode sender
        result[start] = Conversion.intToByteArray(head.getSender().getCode(), 24, new byte[1], 0, Sender.SIZE)[0];
        start += Sender.SIZE;

        //encode control signal
        switch (head.getSender()) {
            case CLIENT:
                result[start] = Conversion.intToByteArray(head.getRequest().getCode(), 24, new byte[1], 0, Request.SIZE)[0];
                start += Request.SIZE;
                break;
            case SERVER:
                result[start] = Conversion.intToByteArray(head.getRespond().getCode(), 24, new byte[1], 0, Respond.SIZE)[0];
                start += Respond.SIZE;
                break;
            default:
                throw new IllegalArgumentException("Not support sender: " + head.getSender());
        }

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
        logger.debug("decode: {}", logP);
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
        head.setSender(Sender.valueOf((int) headBytes[start]));
        start += Sender.SIZE;

        //parse control signal
        switch (head.getSender()) {
            case CLIENT:
                head.setRequest(Request.valueOf((int) headBytes[start]));
                start += Request.SIZE;
                break;
            case SERVER:
                head.setRespond(Respond.valueOf((int) headBytes[start]));
                start += Request.SIZE;
                break;
            default:
                throw new IllegalArgumentException("Not support sender: " + head.getSender());
        }

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