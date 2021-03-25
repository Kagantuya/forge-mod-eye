package com.fudansteam.thread;

import com.fudansteam.Eye;
import com.fudansteam.danmu.entity.DanMu;
import net.minecraft.client.Minecraft;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-23 11:04:49
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class ScrollDanMuThread extends Thread {
    
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                // 遍历中不断移动弹幕，为滚动弹幕更新渲染用的数据
                for (DanMu danMu : Eye.CanRenderDanMuQueue) {
                    String text = danMu.getText();
                    danMu.setX(danMu.getX() - 1);
                    Minecraft client = Minecraft.getInstance();
                    if (danMu.getX() == -client.fontRenderer.getStringWidth(text)) {
                        Eye.CanRenderDanMuQueue.remove(danMu);
                    }
                }
                Thread.sleep(35);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
