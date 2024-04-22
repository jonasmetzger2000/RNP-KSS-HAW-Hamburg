package modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.channel.ChannelHandler.*;

@Sharable
public class ConnectionLimiter extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger connections = new AtomicInteger();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int val = connections.incrementAndGet();
        if (val <= 2) {
            super.channelActive(ctx);
        } else {
            ctx.writeAndFlush("ERROR NO_CONNECTIONS_AVAILABLE\n");
            ctx.close();
        }
    }



    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        connections.decrementAndGet();
    }
}