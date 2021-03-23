package com.fudansteam.events;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-16 23:13:02
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class HudEvents extends AbstractGui {
    
    private static final int PADDING = 14;
    public static final int BLACK_BELT_HEIGHT = 23;
    private static final int RGB = Color.BLACK.getRGB();
    private static final int Z_INDEX = -100;
    
    @SubscribeEvent
    public void onBlackBelt(RenderGameOverlayEvent.Pre e) {
        if (EyeConfig.blackBelt && e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
            this.setBlitOffset(Z_INDEX);
            fillGradient(e.getMatrixStack(), 0, 0, mainWindow.getScaledWidth(), BLACK_BELT_HEIGHT, RGB, RGB);
            fillGradient(e.getMatrixStack(), 0, mainWindow.getScaledHeight() - BLACK_BELT_HEIGHT, mainWindow.getScaledWidth(), mainWindow.getScaledHeight(), RGB, RGB);
        }
    }
    
    @SubscribeEvent
    public void onEyeRender(RenderGameOverlayEvent e) {
        if (Eye.tips != null && e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Minecraft client = Minecraft.getInstance();
            FontRenderer textRenderer = client.fontRenderer;
            int x = client.getMainWindow().getScaledWidth() / 2;
            int y = client.getMainWindow().getScaledHeight() - 59;
            if (client.playerController != null && !client.playerController.shouldDrawHUD()) {
                y += PADDING;
            }
            
            String text;
            if (canRender(EyeConfig.TERRIBLE)) {
                text = Eye.tips.get(EyeConfig.TERRIBLE);
                textRenderer.drawStringWithShadow(
                        e.getMatrixStack(), text,
                        x - (float) textRenderer.getStringWidth(text) / 2,
                        y - (textRenderer.FONT_HEIGHT + PADDING) * 2,
                        getColor(EyeConfig.TERRIBLE));
            }
            if (canRender(EyeConfig.OTHER)) {
                text = Eye.tips.get(EyeConfig.OTHER);
                textRenderer.drawStringWithShadow(
                        e.getMatrixStack(), text,
                        x - (float) textRenderer.getStringWidth(text) / 2,
                        y - (textRenderer.FONT_HEIGHT + PADDING),
                        getColor(EyeConfig.OTHER));
            }
        }
    }
    
    /**
     * 获取提示信息剩余时间对应的颜色值
     *
     * @param tipType 提示种类
     * @return 颜色值
     */
    private int getColor(String tipType) {
        float delta = System.currentTimeMillis() - Eye.tipTimes.get(tipType);
        // 渐变淡出
        int p = MathHelper.floor(MathHelper.clampedLerp(25.0D, 255.0D, (EyeConfig.DISAPPEAR_TIME - delta) / EyeConfig.DISAPPEAR_TIME));
        return Eye.shouldWarn && tipType.equals(EyeConfig.TERRIBLE) ?
                new Color(255, 0, 0, p).getRGB() :
                new Color(255, 255, 255, p).getRGB();
    }
    
    /**
     * 是否可渲染
     *
     * @param tipType 提示种类
     * @return 是否可渲染
     */
    private boolean canRender(String tipType) {
        return Eye.tips.get(tipType) != null && Eye.tipTimes.get(tipType) != null;
    }
    
}
