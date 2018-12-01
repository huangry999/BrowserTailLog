package com.log.socket.logp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.socket.codec.LogProtocolCodec;
import com.log.socket.constants.Mode;
import com.log.socket.constants.Request;
import com.log.socket.constants.Sender;
import com.log.socket.logp.head.*;
import io.netty.handler.codec.CodecException;
import org.apache.logging.log4j.util.Strings;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogPFactory {
    private LogP logP;
    private Map<String, Object> dataMap = new ConcurrentHashMap<>();

    /**
     * Init with default parameters:
     * <ul>
     * <li>main version 1</li>
     * <li>sub version 1</li>
     * <li>total level 1</li>
     * <li>sub level 1</li>
     * <li>control signal Server None</li>
     * <li>mode None</li>
     * </ul>
     *
     * @return
     */
    public static LogPFactory defaultInstance0() {
        LogPFactory factory = new LogPFactory();

        FrameHead head = factory.logP.getHead();
        Version version = new Version();
        version.setMainVersion((short) 1);
        version.setSubVersion((short) 1);
        head.setVersion(version);
        Level level = new Level();
        level.setCurrent((short) 1);
        level.setTotal((short) 1);
        head.setLevel(level);
        ControlSignal signal = new ControlSignal();
        signal.setSender(Sender.SERVER);
        signal.setRequest(Request.NONE);
        head.setControlSignal(signal);
        head.setMode(Mode.NONE);
        return factory;
    }

    public LogPFactory(LogP logP) {
        this.logP = logP;
    }

    public LogPFactory() {
        this.logP = new LogP();
        FrameHead head = new FrameHead();
        head.setStartFlag(new StartFlag(StartFlag.START_FLAG));
        head.setChecksum(new Checksum((short) 0));
        this.logP.setHead(head);
    }

    public LogPFactory setVersion(Version version) {
        this.logP.getHead().setVersion(version);
        return this;
    }

    public LogPFactory setLevel(Level level) {
        this.logP.getHead().setLevel(level);
        return this;
    }

    public LogPFactory setControlSignal(ControlSignal controlSignal) {
        this.logP.getHead().setControlSignal(controlSignal);
        return this;
    }

    public LogPFactory setMode(Mode mode) {
        this.logP.getHead().setMode(mode);
        return this;
    }

    /**
     * Add data to body, ignore if filed is null or empty
     *
     * @param field field name
     * @param data  data
     * @return factory
     */
    public LogPFactory addData(String field, Object data) {
        if (Strings.isBlank(field)) {
            return this;
        }
        dataMap.put(field, data);
        return this;
    }

    /**
     * Generate the log protocol frame
     *
     * @return frame
     */
    public LogP create() throws JsonProcessingException {
        FrameHead head = this.logP.getHead();
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyStr = objectMapper.writeValueAsString(this.dataMap);
        this.logP.setBody(bodyStr);

        ByteBuffer byteBuffer = LogProtocolCodec.CHARSET.encode(bodyStr);
        int bodySize = byteBuffer.limit();
        int size = FrameHead.SIZE + bodySize;
        if (size > (Math.pow(2, Size.SIZE * 8))) {
            throw new CodecException("total size is grate than maximum");
        }
        head.setSize(new Size((short) size));
        return this.logP;
    }

}
