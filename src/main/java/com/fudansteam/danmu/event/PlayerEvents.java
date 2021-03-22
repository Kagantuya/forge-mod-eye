package com.fudansteam.danmu.event;

import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.utils.DanMuOperations;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-20 21:50:25
 * Copyright 2021 HDU_IES. All rights reserved.
 */
@Mod.EventBusSubscriber
public class PlayerEvents {
    
    @SubscribeEvent
    public static void onEnterWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (EyeConfig.danMu) {
            DanMuOperations.open();
        }
    }
    
    @SubscribeEvent
    public static void onEnterWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        DanMuOperations.close();
    }
    
}
