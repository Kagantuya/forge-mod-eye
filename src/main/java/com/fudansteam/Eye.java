package com.fudansteam;

import com.fudansteam.config.EyeDistributor;
import com.fudansteam.danmu.webSocket.WebSocketClient;
import com.fudansteam.events.HudEvents;
import com.fudansteam.events.PlayerEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    public static ConcurrentHashMap<String, String> tips = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> tipTimes = new ConcurrentHashMap<>();
    public static boolean shouldWarn = false;
    public static double originGamma = -1;
    public static boolean entered = false;
    
    /**
     * 弹幕缓存
     */
    public static ScheduledFuture<?> heartBeatTask = null;
    public static WebSocketClient webSocketClient = null;
    
    /**
     * 登录缓存
     */
    public static Map<String, String> loginCookies = null;
    public static String oauthKey = null;
    public static String username = null;
    
    public Eye() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new HudEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        
        EyeDistributor.load();
    }
    
}
