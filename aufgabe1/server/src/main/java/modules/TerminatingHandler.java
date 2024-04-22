package modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class TerminatingHandler extends ChannelInboundHandlerAdapter {

    private final AtomicBoolean terminating;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (terminating.get()) {
            ctx.pipeline().addLast(new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(IdleStateHandler.class);
        ctx.fireChannelInactive();
    }
}
