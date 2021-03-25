package com.fudansteam;

import com.fudansteam.config.EyeDistributor;
import com.fudansteam.danmu.entity.DanMu;
import com.fudansteam.danmu.webSocket.WebSocketClient;
import com.fudansteam.events.HudEvents;
import com.fudansteam.events.PlayerEvents;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Kagantuya
 */
@Mod(Eye.ID)
public class Eye {
    
    public static final String ID = "eye";
    
    /**
     * 模组主缓存
     */
    public static Map<String, String> tipMap = new ConcurrentHashMap<>();
    public static Map<String, Long> tipTimeMap = new ConcurrentHashMap<>();
    public static boolean shouldWarn = false;
    public static double originGamma = -1;
    public static boolean entered = false;
    public static Map<String, Integer> preClosestEntityIdMap = new HashMap<>();
    
    /**
     * 弹幕缓存
     */
    public static ScheduledFuture<?> heartBeatTask = null;
    public static WebSocketClient webSocketClient = null;
    public static Thread scrollDanMuThread = null;
    public static Queue<String> OriginDanMuQueue = new ConcurrentLinkedQueue<>();
    public static Queue<DanMu> CanRenderDanMuQueue = new ConcurrentLinkedQueue<>();
    
    /**
     * 登录缓存
     */
    public static Map<String, String> loginCookieMap = null;
    public static String oauthKey = null;
    public static String username = null;
    public static String informKey = null;
    public static boolean confirmed = false;
    public static DynamicTexture qrImage = null;
    
    public Eye() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new HudEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        
        EyeDistributor.load();
    }
    
}
