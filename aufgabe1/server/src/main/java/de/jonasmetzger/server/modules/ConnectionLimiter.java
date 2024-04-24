package de.jonasmetzger.server.modules;

import de.jonasmetzger.server.msg.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.channel.ChannelHandler.*;

@Sharable
public class ConnectionLimiter extends ChannelInboundHandlerAdapter {

    final static Logger logger = LogManager.getLogger(ConnectionLimiter.class);

    private static final AtomicInteger connections = new AtomicInteger();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int val = connections.incrementAndGet();
        if (val <= 2) {
            super.channelActive(ctx);
        } else {
            logger.warn("Connections Threshold reached, terminating new Client {}", ctx.channel().id().asShortText());
            ctx.writeAndFlush(Response.noConnectionsAvailable());
            ctx.close();
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        connections.decrementAndGet();
    }
}