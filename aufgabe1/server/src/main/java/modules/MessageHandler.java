package modules;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import msg.MessageParser;
import msg.Response;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class MessageHandler extends ChannelInboundHandlerAdapter {

    private final MessageParser parser = new MessageParser();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String str) {
            final Response response = parser.parse(str);
            if (response.isError()) ctx.write("ERROR ");
            else ctx.write("OK ");
            ctx.write(response.getMsg());
            ctx.write("\n");
            if (response.isTermination()) ctx.fireExceptionCaught(new ServerTerminationException());
            if (response.isSessionEnd()) ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    static class ServerTerminationException extends Throwable {}
}
