package org.example.websocket.pingclient;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author duyenthaind
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = Logger.getLogger("WebSocketClientHandler");

    private final WebSocketClientHandshaker handShaker;
    private ChannelPromise handShakeFuture;

    public WebSocketClientHandler(WebSocketClientHandshaker handShaker) {
        this.handShaker = handShaker;
    }

    public ChannelFuture handShakeFuture() {
        return handShakeFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handShaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handShakeFuture = ctx.newPromise();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Channel ch = ctx.channel();
        if (!handShaker.isHandshakeComplete()) {
            // web socket client connected
            handShaker.finishHandshake(ch, (FullHttpResponse) msg);
            handShakeFuture.setSuccess();
            return;
        }

        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.getStatus() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            final TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            // uncomment to print request
            LOGGER.info(textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            LOGGER.info("Pong pong " + frame.toString());
        } else if (frame instanceof CloseWebSocketFrame)
            ch.close();
        else if (frame instanceof BinaryWebSocketFrame) {
            // uncomment to print request
            LOGGER.info(frame.content().toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.log(Level.SEVERE, "Error ", cause);

        if (handShakeFuture.isDone()) {
            handShakeFuture.setFailure(cause);
        }

        ctx.close();
    }
}
