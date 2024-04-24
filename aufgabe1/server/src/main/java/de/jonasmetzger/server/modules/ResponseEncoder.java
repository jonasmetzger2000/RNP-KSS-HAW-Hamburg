package de.jonasmetzger.server.modules;

import de.jonasmetzger.server.msg.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ResponseEncoder extends MessageToMessageEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, List<Object> list) {
        list.add(response.toString());
    }
}
