package modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class IdleConnectionCloseHandler extends IdleStateHandler {
    public IdleConnectionCloseHandler(int allIdleTimeSeconds) {
        super(0, 0, allIdleTimeSeconds);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        if (evt.state().equals(IdleState.ALL_IDLE)) {
            ctx.close();
        }
    }
}
