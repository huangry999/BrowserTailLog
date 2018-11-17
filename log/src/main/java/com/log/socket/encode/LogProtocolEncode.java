package com.log.socket.encode;

import com.log.socket.logp.constants.Mode;
import com.log.socket.logp.head.*;
import com.log.util.LogProtocolUtils;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import com.log.util.Conversion;
import org.apache.commons.lang3.Validate;

import javax.validation.constraints.NotNull;

public class LogProtocolEncode extends ChannelOutboundHandlerAdapter {

    public byte[] encodeHead(@NotNull FrameHead head) {
        byte[] result = new byte[FrameHead.SIZE];
        int start = 0;

        //encode start flag
        Conversion.shortToByteArray(head.getStartFlag().getValue(), 0, result, start, StartFlag.SIZE);
        start += StartFlag.SIZE;

        //encode version
        boolean[] versionBinary = new boolean[ProtocolVersion.SIZE * 8];
        short mv = head.getVersion().getMainVersion();
        short sv = head.getVersion().getSubVersion();
        Conversion.shortToBinary(mv, 12, versionBinary, 0, 4);
        Conversion.shortToBinary(sv, 12, versionBinary, 4, 4);
        result[start] = Conversion.binaryToByte(versionBinary, 0, (byte) 0, 8);
        start += ProtocolVersion.SIZE;

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
        result[start] = Conversion.binaryToByte(controlBinary, 0, (byte) 0 , 8);
        start += ControlSignal.SIZE;

        //encode mode
        result[start] = Conversion.intToByteArray(head.getMode().getFlag(), 24, new byte[1], 0, Mode.SIZE)[0];
        start += Mode.SIZE;

        //encode data package size
        Conversion.shortToByteArray(head.getDataPackageSize().getValue(), 0, result, start, DataPackageSize.SIZE);
        start += DataPackageSize.SIZE;

        int checksum = LogProtocolUtils.calculateChecksum(result);
        Conversion.intToByteArray(checksum, 16, result, start, Checksum.SIZE);
        start += Checksum.SIZE;
        Validate.isTrue(start == FrameHead.SIZE);
        return result;
    }
}
