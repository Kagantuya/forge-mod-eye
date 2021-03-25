package com.fudansteam.events;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.utils.DanMuOperations;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-16 22:59:03
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class PlayerEvents {
    
    @SubscribeEvent
    public void onEnterWorld(PlayerEvent.PlayerLoggedInEvent event) {
        Eye.entered = true;
        if (EyeConfig.instance.enableDanMu) {
            DanMuOperations.open();
        }
    }
    
    @SubscribeEvent
    public void onOutWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        Eye.entered = false;
        if (Eye.scrollDanMuThread != null) {
            Eye.scrollDanMuThread.interrupt();
            Eye.scrollDanMuThread = null;
        }
        DanMuOperations.close();
        Eye.tipMap.clear();
        Eye.tipTimeMap.clear();
        Eye.shouldWarn = false;
        Eye.preClosestEntityIdMap.clear();
    
        Eye.OriginDanMuQueue.clear();
        Eye.CanRenderDanMuQueue.clear();
    }
    
}
