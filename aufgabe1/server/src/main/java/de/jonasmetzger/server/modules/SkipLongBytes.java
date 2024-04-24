package de.jonasmetzger.server.modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkipLongBytes extends ChannelInboundHandlerAdapter {

    final static Logger logger = LogManager.getLogger(SkipLongBytes.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException tooLongFrameException) {
            logger.error("Client {} sended too many bytes", ctx.channel().id().asShortText(), tooLongFrameException);
            ctx.writeAndFlush("ERROR 255_BYTES_LIMIT\n");
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
