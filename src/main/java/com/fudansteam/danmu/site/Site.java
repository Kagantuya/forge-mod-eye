package com.fudansteam.danmu.site;

import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.Config;
import com.fudansteam.danmu.event.SendDanMuEvent;
import com.fudansteam.danmu.utils.MessageCompiler;
import com.fudansteam.danmu.utils.Zlib;
import com.fudansteam.danmu.webSocket.WebSocketClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Kagantuya
 */
public class Site {
    
    private static final long HEART_BEAT_INTERVAL = 30 * 1000;
    private static final int HEADER_LENGTH = 16;
    private static final int SEQUENCE_ID = 1;
    private static final int PACKET_LENGTH_OFFSET = 0;
    private static final int PROTOCOL_VERSION_OFFSET = 6;
    private static final int OPERATION_OFFSET = 8;
    private static final int BODY_OFFSET = 16;
    private static final int JSON_PROTOCOL_VERSION = 0;
    private static final int POPULAR_PROTOCOL_VERSION = 1;
    private static final int BUFFER_PROTOCOL_VERSION = 2;
    private static final int HEART_BEAT_OPERATION = 2;
    private static final int MESSAGE_OPERATION = 5;
    private static final int ENTER_ROOM_OPERATION = 7;
    private final Gson gson;
    private final Config config;
    
    public Site() {
        this.config = new Config();
        this.gson = new GsonBuilder().registerTypeAdapter(String.class, new MessageDeserializer(config)).create();
    }
    
    public void initMessage(WebSocketClient client) {
        int id = RoomId.getRealRoomId(EyeConfig.roomId);
        if (id == -1) {
            MinecraftForge.EVENT_BUS.post(new SendDanMuEvent(new TranslationTextComponent("eye.inform.link_live_room.fail", EyeConfig.roomId).getString()));
        } else {
            MinecraftForge.EVENT_BUS.post(new SendDanMuEvent(new TranslationTextComponent("eye.inform.link_live_room.suc", EyeConfig.roomId).getString()));
        }
        byte[] message = String.format("{\"roomid\": %d}", id).getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(HEADER_LENGTH + message.length);
        buf.writeShort(HEADER_LENGTH);
        buf.writeShort(BUFFER_PROTOCOL_VERSION);
        buf.writeInt(ENTER_ROOM_OPERATION);
        buf.writeInt(SEQUENCE_ID);
        buf.writeBytes(message);
        client.sendMessage(buf);
    }
    
    public long getHeartBeatInterval() {
        return HEART_BEAT_INTERVAL;
    }
    
    public ByteBuf getHeartBeat() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(HEADER_LENGTH);
        buf.writeShort(HEADER_LENGTH);
        buf.writeShort(BUFFER_PROTOCOL_VERSION);
        buf.writeInt(HEART_BEAT_OPERATION);
        buf.writeInt(SEQUENCE_ID);
        return buf;
    }
    
    public void handleMessage(WebSocketFrame webSocketFrame) throws Exception {
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            ByteBuf data = webSocketFrame.content();
            int protocol = data.getShort(PROTOCOL_VERSION_OFFSET);
            switch (protocol) {
                case JSON_PROTOCOL_VERSION:
                case POPULAR_PROTOCOL_VERSION:
                    return;
                case BUFFER_PROTOCOL_VERSION:
                    handleBufferMessage(data);
                    return;
                default:
            }
        }
    }
    
    private void handleBufferMessage(ByteBuf data) throws Exception {
        int packetLength = data.getInt(PACKET_LENGTH_OFFSET);
        int operation = data.getInt(OPERATION_OFFSET);
        
        if (operation == MESSAGE_OPERATION) {
            byte[] uncompressedData = new byte[packetLength - BODY_OFFSET];
            data.getBytes(BODY_OFFSET, uncompressedData);
            byte[] decompressData = Zlib.decompress(uncompressedData);
            byte[] msgBytes = Arrays.copyOfRange(decompressData, BODY_OFFSET, decompressData.length);
            String[] message = MessageCompiler.split(IOUtils.toString(msgBytes, StandardCharsets.UTF_8.toString()));
            for (String msg : message) {
                handleStringMessage(msg);
            }
        }
    }
    
    private void handleStringMessage(String message) {
        try {
            String str = gson.fromJson(message, String.class);
            if (str != null) {
                MinecraftForge.EVENT_BUS.post(new SendDanMuEvent(str));
            }
        } catch (JsonSyntaxException ignore) {
        }
    }
    
    public Config getConfig() {
        return config;
    }
    
}
