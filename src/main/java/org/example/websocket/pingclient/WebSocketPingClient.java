package org.example.websocket.pingclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;

import java.net.URI;
import java.util.logging.Logger;

/**
 * @author duyenthaind
 */
public class WebSocketPingClient {

    private static final Logger LOGGER = Logger.getLogger("WebSocketPingClient");

    private final URI uri;
    private Channel channel;
    private static final EventLoopGroup group = new NioEventLoopGroup();

    public WebSocketPingClient(String uri) {
        this.uri = URI.create(uri);
    }

    public void open() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        String protocol = uri.getScheme();
        if (!"ws".equals(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol " + protocol);
        }
        final WebSocketClientHandler handler = new WebSocketClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null,
                        false, HttpHeaders.EMPTY_HEADERS, 1280000));
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(265536));
                        ch.pipeline().addLast(handler);
                    }
                });
        channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handShakeFuture().sync();
    }

    public void close() throws Exception {
        channel.writeAndFlush(new CloseWebSocketFrame());
        channel.closeFuture().sync();
        group.shutdownGracefully();
    }

    public void ping() throws Exception {
        LOGGER.info("Ping!");
//        channel.writeAndFlush(new PingWebSocketFrame());
        channel.writeAndFlush(new TextWebSocketFrame("ping"));
    }
}
