package com.log.uiapi.protocol.codec;

import com.log.common.conversion.ByteConversion;
import com.log.common.printer.BytePrinter;
import com.log.uiapi.protocol.constants.*;
import com.log.uiapi.protocol.exception.LogPException;
import com.log.uiapi.protocol.logp.LogP;
import com.log.uiapi.protocol.logp.head.*;
import com.log.uiapi.protocol.util.LogProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
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
import java.util.Objects;

@ChannelHandler.Sharable
public class LogProtocolCodec extends MessageToMessageCodec<BinaryWebSocketFrame, LogP> {
    private final int lengthFieldEndOffset;
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
        ByteConversion.shortToByteArray(head.getStartFlag().getValue(), 0, result, start, StartFlag.SIZE);
        start += StartFlag.SIZE;

        //encode size
        if (head.getSize() != null) {
            ByteConversion.shortToByteArray(head.getSize().getValue(), 0, result, start, Size.SIZE);
        }
        start += Size.SIZE;

        //encode version
        boolean[] versionBinary = new boolean[Version.SIZE * 8];
        short mv = head.getVersion().getMainVersion();
        short sv = head.getVersion().getSubVersion();
        ByteConversion.shortToBinary(mv, 12, versionBinary, 0, 4);
        ByteConversion.shortToBinary(sv, 12, versionBinary, 4, 4);
        result[start] = ByteConversion.binaryToByte(versionBinary, 0, (byte) 0, 8);
        start += Version.SIZE;

        //encode sender
        result[start] = ByteConversion.intToByteArray(head.getSender().getCode(), 24, new byte[1], 0, Sender.SIZE)[0];
        start += Sender.SIZE;

        //encode control signal
        if (Objects.nonNull(head.getRequest())) {
            result[start] = ByteConversion.intToByteArray(head.getRequest().getCode(), 24, new byte[1], 0, Request.SIZE)[0];
            start += Request.SIZE;
        }
        if (Objects.nonNull(head.getRespond())) {
            result[start] = ByteConversion.intToByteArray(head.getRespond().getCode(), 24, new byte[1], 0, Respond.SIZE)[0];
            start += Respond.SIZE;
        }

        //encode mode
        if (head.getMode() != null) {
            result[start] = ByteConversion.intToByteArray(head.getMode().getFlag(), 24, new byte[1], 0, Mode.SIZE)[0];
        }
        start += Mode.SIZE;

        //encode checksum
        int checksum = LogProtocolUtils.calculateChecksum(result);
        ByteConversion.intToByteArray(checksum, 16, result, start, Checksum.SIZE);
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
        byte[] headByte = new byte[FrameHead.SIZE];
        in.readBytes(headByte);
        LogP logP = new LogP();
        FrameHead head = this.decodeHead(headByte);
        logP.setHead(head);
        String body = in.readCharSequence(head.getSize().getValue() - FrameHead.SIZE, CHARSET).toString();
        logP.setBody(body);
        logger.debug("\n head frame:{}\n body:{}", BytePrinter.toString1(headByte), body);
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
        Validate.isTrue(headBytes.length == FrameHead.SIZE);
        FrameHead head = new FrameHead();
        int start = 0;

        //validate checksum
        if (LogProtocolUtils.calculateChecksum(headBytes) != 0) {
            throw new LogPException(RespondStatus.DECODE_ERROR, "checksum error: " + BytePrinter.toString(headBytes));
        }

        //parse start flag
        short sfv = ByteConversion.byteArrayToShort(headBytes, start, (short) 0, StartFlag.SIZE);
        StartFlag startFlag = new StartFlag(sfv);
        head.setStartFlag(startFlag);
        start += StartFlag.SIZE;

        //parse size
        short sizeVal = ByteConversion.byteArrayToShort(headBytes, start, (short) 0, Size.SIZE);
        Size size = new Size(sizeVal);
        start += Size.SIZE;
        head.setSize(size);

        //parse version
        boolean[] versionBit = new boolean[8];
        ByteConversion.byteToBinary(headBytes[start], 0, versionBit, 0, Version.SIZE * 8);
        start += Version.SIZE;
        Version version = new Version();
        version.setMainVersion(ByteConversion.binaryToShort(versionBit, 0, (short) 0, 4));
        version.setSubVersion(ByteConversion.binaryToShort(versionBit, 4, (short) 0, 4));
        head.setVersion(version);

        //parse data level
        head.setSender(Sender.valueOf((int) headBytes[start]));
        start += Sender.SIZE;

        //parse control signal
        if (head.getSender() == Sender.CLIENT) {
            head.setRequest(Request.valueOf((int) headBytes[start]));
            start += Request.SIZE;
        } else if (head.getSender() == Sender.SERVER) {
            head.setRespond(Respond.valueOf((int) headBytes[start]));
            start += Request.SIZE;
        }

        //parse mode
        head.setMode(Mode.valueOf(headBytes[start]));
        start += Mode.SIZE;

        //parse checksum
        short cv = ByteConversion.byteArrayToShort(headBytes, start, (short) 0, Checksum.SIZE);
        Checksum checksum = new Checksum(cv);
        start += Checksum.SIZE;
        head.setChecksum(checksum);

        Validate.isTrue(start == FrameHead.SIZE);

        return head;
    }
}
