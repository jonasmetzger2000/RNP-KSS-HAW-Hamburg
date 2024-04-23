package modules;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
                context.pipeline().addFirst(new TestHandler(5));
            }
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
