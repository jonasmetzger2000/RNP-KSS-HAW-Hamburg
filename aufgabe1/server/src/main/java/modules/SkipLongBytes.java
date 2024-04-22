package modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;

public class SkipLongBytes extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof TooLongFrameException)) {
            super.exceptionCaught(ctx, cause);
        }
        ctx.writeAndFlush("ERROR 255_BYTES_LIMIT\n");
    }
}
