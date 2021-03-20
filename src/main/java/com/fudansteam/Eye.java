package com.fudansteam;

import com.fudansteam.config.EyeDistributor;
import com.fudansteam.events.EntityEvents;
import com.fudansteam.events.HudEvents;
import com.fudansteam.events.OptionsScreenEvents;
import com.fudansteam.events.WorldEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kagantuya
 */
@Mod(Eye.ID)
public class Eye {
    
    public static final String ID = "eye";
    
    public static ConcurrentHashMap<String, String> tips = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> tipTimes = new ConcurrentHashMap<>();
    public static boolean shouldWarn = false;
    public static double originGamma = -1;
    public static boolean unload = false;
    public static Queue<String> danMu = new ArrayDeque<>();
    
    public Eye() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        MinecraftForge.EVENT_BUS.register(new WorldEvents());
        MinecraftForge.EVENT_BUS.register(new HudEvents());
        MinecraftForge.EVENT_BUS.register(new OptionsScreenEvents());
        EyeDistributor.load();
    }
    
}
