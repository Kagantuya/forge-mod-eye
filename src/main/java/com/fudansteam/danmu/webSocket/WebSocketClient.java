package com.fudansteam.danmu.webSocket;

import com.fudansteam.Eye;
import com.fudansteam.danmu.site.Site;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kagantuya
 */
public class WebSocketClient {
    
    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
    private final Site site;
    private final URI uri;
    private Channel channel;
    
    public WebSocketClient(Site site) {
        this.site = site;
        this.uri = URI.create("wss://broadcastlv.chat.bilibili.com:443/sub");
    }
    
    public void open() throws Exception {
        SslContext sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        EventLoopGroup group = new NioEventLoopGroup();
        WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE);
        WebSocketClientHandler handler = new WebSocketClientHandler(handShaker, site);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipe = ch.pipeline();
                        pipe.addLast(
                                sslCtx.newHandler(ch.alloc(), uri.getHost(), uri.getPort()),
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                handler
                        );
                    }
                });
        channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();
        site.initMessage(this);
        Eye.heartBeatTask = SERVICE.scheduleAtFixedRate(() -> sendMessage(site.getHeartBeat()), site.getHeartBeatInterval(), site.getHeartBeatInterval(), TimeUnit.MILLISECONDS);
    }
    
    public void close() throws InterruptedException {
        channel.writeAndFlush(new CloseWebSocketFrame());
        channel.closeFuture().sync();
        Eye.heartBeatTask.cancel(true);
    }
    
    public void sendMessage(final ByteBuf binaryData) {
        channel.writeAndFlush(new BinaryWebSocketFrame(binaryData));
    }
    
}
