package com.log.socket.logp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.socket.codec.LogProtocolCodec;
import com.log.socket.constants.Mode;
import com.log.socket.constants.Request;
import com.log.socket.constants.Respond;
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
        head.setSender(Sender.SERVER);
        head.setRespond(Respond.NONE);
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

    public LogPFactory setSender(Sender sender) {
        this.logP.getHead().setSender(sender);
        return this;
    }

    public LogPFactory setRespond(Respond respond) {
        this.logP.getHead().setRespond(respond);
        return this;
    }

    public LogPFactory setRequest(Request request) {
        this.logP.getHead().setRequest(request);
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
        if (Strings.isBlank(field) || data == null) {
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
    public LogP create() {
        ObjectMapper objectMapper = new ObjectMapper();
        String bodyStr;
        try {
            bodyStr = objectMapper.writeValueAsString(this.dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.logP.setBody(bodyStr);

        /*FrameHead head = this.logP.getHead();
        ByteBuffer byteBuffer = LogProtocolCodec.CHARSET.encode(bodyStr);
        int bodySize = byteBuffer.limit();
        int size = FrameHead.SIZE + bodySize;
        if (size > (Math.pow(2, Size.SIZE * 8))) {
            throw new CodecException(String.format("total size %s is grate than maximum %s", size, Math.pow(2, Size.SIZE * 8)));
        }
        head.setSize(new Size((short) size));*/
        return this.logP;
    }

}
