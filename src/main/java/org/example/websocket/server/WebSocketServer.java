package org.example.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

import java.util.logging.Logger;

/**
 * @author duyenthaind
 */
public class WebSocketServer {

    private static final Logger LOGGER = Logger.getLogger("WebSocketServer");

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    private ServerBootstrap serverBootstrap;

    public void start(int port) throws Exception{
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(265536));
                        ch.pipeline().addLast(new WebSocketServerCompressionHandler());
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
                        ch.pipeline().addLast(new WebSocketFrameHandler());
                    }
                });
        serverBootstrap.bind(port).sync().channel();

        LOGGER.info("WebSocket is started at port : " + port);
    }
}
