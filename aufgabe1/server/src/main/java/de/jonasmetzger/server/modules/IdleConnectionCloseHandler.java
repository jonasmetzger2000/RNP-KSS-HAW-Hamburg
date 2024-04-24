package de.jonasmetzger.server.modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdleConnectionCloseHandler extends IdleStateHandler {

    final static Logger logger = LogManager.getLogger(IdleConnectionCloseHandler.class);

    public IdleConnectionCloseHandler(int allIdleTimeSeconds) {
        super(0, 0, allIdleTimeSeconds);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        if (evt.state().equals(IdleState.ALL_IDLE)) {
            ctx.close();
            logger.info("Client {} was closed due to Idle timeout", ctx.channel().id().asShortText());
        }
    }
}
