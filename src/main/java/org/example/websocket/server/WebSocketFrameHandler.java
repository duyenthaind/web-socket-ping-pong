package org.example.websocket.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.logging.Logger;

/**
 * @author duyenthaind
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger LOGGER = java.util.logging.Logger.getLogger("WebSocketFrameHandler");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        LOGGER.info(msg + "");
        if (msg instanceof TextWebSocketFrame) {
//            ctx.channel().writeAndFlush(new TextWebSocketFrame("Hi"));
        } else if (msg instanceof PingWebSocketFrame) {
            LOGGER.info("Ignore ping socket frame");
        }
    }
}
