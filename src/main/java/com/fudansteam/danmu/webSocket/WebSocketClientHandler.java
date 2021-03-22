package com.fudansteam.danmu.webSocket;

import com.fudansteam.Eye;
import com.fudansteam.danmu.site.Site;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

/**
 * @author Kagantuya
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    
    private final Site site;
    private final WebSocketClientHandshaker handShaker;
    private ChannelPromise handshakeFuture;
    
    public WebSocketClientHandler(WebSocketClientHandshaker handShaker, Site site) {
        this.handShaker = handShaker;
        this.site = site;
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handShaker.handshake(ctx.channel());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Eye.heartBeatTask.cancel(true);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handShaker.isHandshakeComplete()) {
            handShaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            return;
        }
        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        final WebSocketFrame frame = (WebSocketFrame) msg;
        site.handleMessage(frame);
    }
    
    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
    
}
