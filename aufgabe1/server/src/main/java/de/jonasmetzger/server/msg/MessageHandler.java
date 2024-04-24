package de.jonasmetzger.server.msg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RequiredArgsConstructor
public class MessageHandler extends ChannelInboundHandlerAdapter {

    final static Logger logger = LogManager.getLogger(MessageHandler.class);

    private final MessageParser parser = new MessageParser();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String str) {
            final Response response = parser.parse(str, ctx.channel().id().asShortText());
            if (response.isError()) {
                logger.info("Client {} sent invalid data, failed with {}", ctx.channel().id().asShortText(), response.getMsg());
            }
            ctx.writeAndFlush(response);
            if (response.isTermination()) ctx.fireExceptionCaught(new ServerTerminationException());
            if (response.isSessionEnd()) ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public static class ServerTerminationException extends Throwable {}
}
