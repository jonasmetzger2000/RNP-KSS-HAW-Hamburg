package modules;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;

public class NewLineFrameDecoder extends LineBasedFrameDecoder {

    public NewLineFrameDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        try {
            return super.decode(ctx, buffer);
        } catch (Exception e) {
            int newlineIndex = ByteBufUtil.indexOf(buffer, buffer.readerIndex(), buffer.writerIndex(), (byte) '\n');
            if (newlineIndex >= 0) {
                int length = newlineIndex - buffer.readerIndex() + 1;
                buffer.skipBytes(length);
            } else {
                buffer.skipBytes(buffer.writerIndex());
            }
            return null;
        }
    }
}
