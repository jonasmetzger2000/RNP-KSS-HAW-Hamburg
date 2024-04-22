import io.github.cdimascio.dotenv.Dotenv;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import modules.ConnectionLimiter;
import modules.MessageHandler;
import modules.SkipLongBytes;
import modules.TerminatingHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyServer {


    public static void main(String[] args) {
        final Dotenv env = Dotenv.load();
        final AtomicBoolean terminating = new AtomicBoolean(false);
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            final ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new TerminatingHandler(terminating));
                            pipeline.addLast(new LineBasedFrameDecoder(Integer.parseInt(env.get("PROT_MAX_BYTE_SIZE"))));
                            pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
                            pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
                            pipeline.addLast(new SkipLongBytes());
                            pipeline.addLast(new ConnectionLimiter());
                            pipeline.addLast(new MessageHandler(terminating));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            final ChannelFuture httpChannel = bootstrap.bind(env.get("TCP_HOST"), Integer.parseInt(env.get("TCP_PORT"))).sync();

            // Wait until server socket is closed
            httpChannel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.err.println("Closed Socket.");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
