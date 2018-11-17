package com.log.socket.codec;

import com.log.socket.decode.LogProtocolDecode;
import com.log.socket.encode.LogProtocolEncode;
import io.netty.channel.CombinedChannelDuplexHandler;

public class LogProtocolCodec extends CombinedChannelDuplexHandler<LogProtocolDecode, LogProtocolEncode> {
}
