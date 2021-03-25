package com.fudansteam.events;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.entity.DanMu;
import com.fudansteam.thread.ScrollDanMuThread;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
    private static final int RGB = Color.BLACK.getRGB();
    private static final int Z_INDEX = -100;
    private static final int BLACK_BELT_HEIGHT = 23;
    private static final String TERRIBLE = "TERRIBLE";
    private static final String OTHER = "OTHER";
    private static final float DISAPPEAR_TIME = 1500.0F;
    
    @SubscribeEvent
    public void onBlackBelt(RenderGameOverlayEvent.Pre e) {
        if (EyeConfig.instance.enableBlackBelt && e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
            this.setBlitOffset(Z_INDEX);
            fillGradient(e.getMatrixStack(), 0, 0, mainWindow.getScaledWidth(), BLACK_BELT_HEIGHT, RGB, RGB);
            fillGradient(e.getMatrixStack(), 0, mainWindow.getScaledHeight() - BLACK_BELT_HEIGHT, mainWindow.getScaledWidth(), mainWindow.getScaledHeight(), RGB, RGB);
        }
    }
    
    @SubscribeEvent
    public void onEyeRender(RenderGameOverlayEvent e) {
        if (Eye.tipMap.size() != 0 && e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Minecraft client = Minecraft.getInstance();
            FontRenderer textRenderer = client.fontRenderer;
            int x = client.getMainWindow().getScaledWidth() / 2;
            int y = client.getMainWindow().getScaledHeight() - 59;
            if (client.playerController != null && !client.playerController.shouldDrawHUD()) {
                y += PADDING;
            }
            
            String text;
            if (canRender(TERRIBLE)) {
                text = Eye.tipMap.get(TERRIBLE);
                textRenderer.drawStringWithShadow(
                        e.getMatrixStack(), text,
                        x - (float) textRenderer.getStringWidth(text) / 2,
                        y - (textRenderer.FONT_HEIGHT + PADDING) * 2,
                        getColor(TERRIBLE));
            }
            if (canRender(OTHER)) {
                text = Eye.tipMap.get(OTHER);
                textRenderer.drawStringWithShadow(
                        e.getMatrixStack(), text,
                        x - (float) textRenderer.getStringWidth(text) / 2,
                        y - (textRenderer.FONT_HEIGHT + PADDING),
                        getColor(OTHER));
            }
        }
    }
    
    @SubscribeEvent
    public void onScrollDanMu(RenderGameOverlayEvent e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.ALL && EyeConfig.instance.enableDanMuScroll) {
            if (Eye.scrollDanMuThread == null) {
                Eye.scrollDanMuThread = new ScrollDanMuThread();
                Eye.scrollDanMuThread.start();
            } else {
                FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
                for (DanMu danMu : Eye.CanRenderDanMuQueue) {
                    int y = (danMu.getLayer() - 1) * (fontRenderer.FONT_HEIGHT + EyeConfig.instance.danMuRowSpacing) + EyeConfig.instance.danMuRowSpacing;
                    if (EyeConfig.instance.enableBlackBelt) {
                        y += BLACK_BELT_HEIGHT;
                    }
                    fontRenderer.drawStringWithShadow(e.getMatrixStack(), danMu.getText(), danMu.getX(), y, Color.WHITE.getRGB());
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
        if (Eye.entered) {
            Entity entity = e.getEntity();
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null || ((int) entity.getDistance(player)) == 0) {
                return;
            }
            // 实体不为当前玩家同时二者距离在指定半径内
            if (entity.getEntityId() != player.getEntityId() && entity.isEntityInRange(player, EyeConfig.instance.distance)) {
                insideEye(entity, player);
            } else {
                outsideEye();
            }
        }
    }
    
    /**
     * 在检测距离之外
     */
    private void outsideEye() {
        // 当前未检测到实体则判断是否可移除过期提示
        for (String tipType : Eye.tipTimeMap.keySet()) {
            if (System.currentTimeMillis() - Eye.tipTimeMap.get(tipType) >= DISAPPEAR_TIME) {
                Eye.tipMap.remove(tipType);
                Eye.tipTimeMap.remove(tipType);
            }
        }
    }
    
    /**
     * 在检测距离之内
     *
     * @param entity 实体
     * @param player 玩家
     */
    private void insideEye(Entity entity, ClientPlayerEntity player) {
        if (entity instanceof MonsterEntity || entity instanceof AmbientEntity || entity instanceof SlimeEntity) {
            shouldUpdate(TERRIBLE, entity, player);
        } else {
            shouldUpdate(OTHER, entity, player);
        }
    }
    
    /**
     * 是否应该更新
     *
     * @param tipType 提示种类
     * @param entity  实体
     * @param player  玩家
     */
    private void shouldUpdate(String tipType, Entity entity, ClientPlayerEntity player) {
        int entityId = entity.getEntityId();
        // 提示实体信息同时跟踪此实体，若之前未标记过实体或标记实体消失了，或接下来有新的实体更靠近玩家或仍为此实体则再次更新实体
        Integer preClosestEntityId = Eye.preClosestEntityIdMap.get(tipType);
        Entity preClosestEntity = null;
        if (Minecraft.getInstance().world != null && preClosestEntityId != null) {
            preClosestEntity = Minecraft.getInstance().world.getEntityByID(preClosestEntityId);
        }
        if (preClosestEntityId == null || preClosestEntity == null ||
                entity.getDistance(player) < preClosestEntity.getDistance(player) || entityId == preClosestEntityId) {
            Eye.tipMap.put(tipType, getTip(player, entity));
            Eye.preClosestEntityIdMap.put(tipType, entityId);
            updateTipTimes(tipType);
            if (tipType.equals(TERRIBLE)) {
                Eye.shouldWarn = entity.getDistance(player) <= EyeConfig.instance.warnDistance;
            }
        }
    }
    
    /**
     * 更新提示触发时间
     *
     * @param tipType 提示种类
     */
    private void updateTipTimes(String tipType) {
        Long preTime = Eye.tipTimeMap.get(tipType);
        if (preTime == null || System.currentTimeMillis() - preTime < DISAPPEAR_TIME) {
            Eye.tipTimeMap.put(tipType, System.currentTimeMillis());
        }
    }
    
    /**
     * 获取提示
     *
     * @param player 玩家
     * @param entity 实体
     * @return 提示
     */
    private String getTip(ClientPlayerEntity player, Entity entity) {
        double leftOrRight = getLeftOrRight(player, entity);
        double upOrDown = player.getPosY() - entity.getPosY();
        String uod = upOrDown < 0.0D ? " ↑ " : upOrDown == 0 ? " " : " ↓ ";
        String dir = leftOrRight < 0.0D ? " <" + uod : leftOrRight == 0 ? "" : uod + "> ";
        return (leftOrRight < 0.0D ? dir : " ") +
                entity.getName().getString() + " : " + (int) entity.getDistance(player) +
                (leftOrRight > 0.0D ? dir : " ") + (leftOrRight == 0 ? uod : "");
    }
    
    /**
     * 获取实体相对玩家左右方位
     *
     * @param player 玩家
     * @param entity 实体
     * @return 方位
     */
    private double getLeftOrRight(ClientPlayerEntity player, Entity entity) {
        Vector3d vector3d = new Vector3d(player.getPosX(), player.getPosYEye(), player.getPosZ());
        Vector3d vector3d1 = (new Vector3d(0.0D, 0.0D, -1.0D)).rotatePitch(-player.rotationPitch * ((float) Math.PI / 180F)).rotateYaw(-player.rotationYaw * ((float) Math.PI / 180F));
        Vector3d vector3d2 = (new Vector3d(0.0D, 1.0D, 0.0D)).rotatePitch(-player.rotationPitch * ((float) Math.PI / 180F)).rotateYaw(-player.rotationYaw * ((float) Math.PI / 180F));
        Vector3d vector3d3 = vector3d1.crossProduct(vector3d2);
        Vector3d vector3d4 = entity.getPositionVec().subtract(vector3d).normalize();
        return -vector3d1.dotProduct(vector3d4) <= 0.5D ? -vector3d3.dotProduct(vector3d4) : 0D;
    }
    
    /**
     * 获取提示信息剩余时间对应的颜色值
     *
     * @param tipType 提示种类
     * @return 颜色值
     */
    private int getColor(String tipType) {
        float delta = System.currentTimeMillis() - Eye.tipTimeMap.get(tipType);
        // 渐变淡出
        int p = MathHelper.floor(MathHelper.clampedLerp(25.0D, 255.0D, (DISAPPEAR_TIME - delta) / DISAPPEAR_TIME));
        return Eye.shouldWarn && tipType.equals(TERRIBLE) ?
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
        return Eye.tipMap.get(tipType) != null && Eye.tipTimeMap.get(tipType) != null;
    }
    
}
