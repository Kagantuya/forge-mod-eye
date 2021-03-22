package com.fudansteam.events;

import com.fudansteam.Eye;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-16 22:59:03
 * Copyright 2021 HDU_IES. All rights reserved.
 */
@Mod.EventBusSubscriber
public class WorldEvents {
    
    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload e) {
        Eye.unload = true;
    }
    
    @SubscribeEvent
    public static void onLoad(WorldEvent.Load e) {
        Eye.unload = false;
    }
    
}
