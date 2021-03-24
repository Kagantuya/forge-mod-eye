package com.fudansteam.thread;

import com.fudansteam.danmu.entity.DanMu;
import com.fudansteam.events.HudEvents;
import net.minecraft.client.Minecraft;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-23 11:04:49
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class ScrollDanMuThread extends Thread {
    
    public static Queue<DanMu> danMuQueue = new ConcurrentLinkedQueue<>();
    
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                // 遍历中不断移动弹幕，为滚动弹幕更新渲染用的数据
                for (DanMu danMu : danMuQueue) {
                    String text = danMu.getText();
                    danMu.setX(danMu.getX() - 1);
                    Minecraft client = Minecraft.getInstance();
                    if (danMu.getX() == -client.fontRenderer.getStringWidth(text)) {
                        danMuQueue.remove(danMu);
                    }
                }
                HudEvents.danMuQueue = danMuQueue;
                Thread.sleep(35);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
