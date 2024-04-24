package de.jonasmetzger.server;

import de.jonasmetzger.server.modules.ConnectionLimiter;
import de.jonasmetzger.server.modules.ResponseEncoder;
import de.jonasmetzger.server.modules.SkipLongBytes;
import de.jonasmetzger.server.modules.TerminatingHandler;
import de.jonasmetzger.server.msg.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class NettyServer {

    final static Logger logger = LogManager.getLogger(NettyServer.class);

    public static void main(String[] args) {
        logger.info("Loading .env configuration");
        Configuration.load();
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            logger.info("Bootstrapping TCP Server on {}:{}", Configuration.TCP_HOST, Configuration.TCP_PORT);
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            final ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new LineBasedFrameDecoder(Configuration.PROT_MAX_BYTE_SIZE));
                            pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
                            pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
                            pipeline.addLast(new ResponseEncoder());
                            pipeline.addLast(new SkipLongBytes());
                            pipeline.addLast(new ConnectionLimiter());
                            pipeline.addLast(new MessageHandler());
                            pipeline.addLast(new TerminatingHandler(bossGroup, workerGroup));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(Configuration.TCP_HOST, Configuration.TCP_PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Server Socket was interrupted", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
