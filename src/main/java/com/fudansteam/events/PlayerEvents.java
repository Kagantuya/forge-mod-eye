package com.fudansteam.events;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.event.SendDanMuEvent;
import com.fudansteam.danmu.utils.DanMuOperations;
import com.fudansteam.thread.ScrollDanMuThread;
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
        if (EyeConfig.danMu) {
            DanMuOperations.open();
        }
    }
    
    @SubscribeEvent
    public void onOutWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        Eye.entered = false;
        if (HudEvents.scrollDanMuThread != null) {
            HudEvents.scrollDanMuThread.interrupt();
            HudEvents.scrollDanMuThread = null;
        }
        DanMuOperations.close();
        Eye.tips.clear();
        Eye.tipTimes.clear();
        Eye.shouldWarn = false;
        HudEvents.preClosestEntityIdMap.clear();
        
        HudEvents.danMuQueue.clear();
        SendDanMuEvent.TEXT_CACHE.clear();
        ScrollDanMuThread.danMuQueue.clear();
    }
    
}
