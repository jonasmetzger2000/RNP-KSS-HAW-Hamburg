package de.jonasmetzger.server.modules;

import de.jonasmetzger.server.msg.MessageHandler;
import de.jonasmetzger.server.msg.Response;
import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static de.jonasmetzger.server.Configuration.TERMINATION_SOCKET_TIMEOUT;
import static io.netty.channel.ChannelHandler.*;

@Sharable
@RequiredArgsConstructor
public class TerminatingHandler extends ChannelInboundHandlerAdapter {

    final static Logger logger = LogManager.getLogger(TerminatingHandler.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private static final List<ChannelHandlerContext> handlers = new ArrayList<>();
    private static boolean terminated = false;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (terminated) {
            logger.info("Terminating new Handler {}", ctx.channel().id().asShortText());
            ctx.writeAndFlush(Response.terminated());
            ctx.close();
        } else {
            logger.info("Adding new Handler {}", ctx.channel().id().asShortText());
            super.channelRegistered(ctx);
            handlers.add(ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        handlers.remove(ctx);
        logger.info("Removed Handler {}", ctx.channel().id().asShortText());
        if (terminated && handlers.isEmpty()) {
            logger.info("Stopping Server...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            logger.info("Exiting Server...");

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof MessageHandler.ServerTerminationException) {
            logger.info("Termination Signal from {}", ctx.channel().id().asShortText());
            for (ChannelHandlerContext context : handlers) {
                context.pipeline().addFirst(new IdleConnectionCloseHandler(TERMINATION_SOCKET_TIMEOUT));
            }
            terminated = true;
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
