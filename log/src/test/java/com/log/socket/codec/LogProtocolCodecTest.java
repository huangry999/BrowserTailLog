package com.log.socket.codec;

import com.log.socket.decode.LogProtocolDecode;
import com.log.socket.encode.LogProtocolEncode;
import com.log.socket.logp.constants.Mode;
import com.log.socket.logp.constants.Request;
import com.log.socket.logp.constants.Sender;
import com.log.socket.logp.head.*;
import com.log.util.PrintUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class LogProtocolCodecTest {

    @Test
    public void testCodec(){
        FrameHead head = new FrameHead();
        head.setStartFlag(new StartFlag(StartFlag.START_FLAG));

        ProtocolVersion version = new ProtocolVersion();
        version.setSubVersion((short)1);
        version.setMainVersion((short)1);
        head.setVersion(version);

        Level level = new Level();
        level.setCurrent((short)1);
        level.setTotal((short)1);
        head.setLevel(level);

        ControlSignal signal = new ControlSignal();
        signal.setSender(Sender.SERVER);
        signal.setRequest(Request.INIT);
        head.setControlSignal(signal);

        head.setMode(Mode.MODIFY);

        head.setDataPackageSize(new DataPackageSize((short)2324));

        head.setChecksum(new Checksum((short)0));

        LogProtocolEncode encode = new LogProtocolEncode();
        byte[] bytes = encode.encodeHead(head);
        System.out.println(PrintUtils.toString(bytes));

        LogProtocolDecode decode = new LogProtocolDecode();
        FrameHead headDec = decode.decodeHead(bytes);
        System.out.println(headDec);

        assertEquals(head.hashCode(), headDec.hashCode());
    }
}
