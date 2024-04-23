package modules;

import io.netty.channel.*;

import java.util.ArrayList;
import java.util.List;

import static io.netty.channel.ChannelHandler.*;

@Sharable
public class TerminatingHandler extends ChannelInboundHandlerAdapter {

    public static List<ChannelHandlerContext> handlers = new ArrayList<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        handlers.add(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof MessageHandler.ServerTerminationException) {
            for (ChannelHandlerContext context : handlers) {
                context.pipeline().addFirst(new IdleConnectionCloseHandler(5));
            }
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
