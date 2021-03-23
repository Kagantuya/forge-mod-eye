package com.fudansteam.events;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.entity.DanMu;
import com.fudansteam.danmu.event.SendDanMuEvent;
import com.fudansteam.danmu.utils.DanMuOperations;
import com.fudansteam.thread.DanMuRenderThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-16 22:59:03
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class PlayerEvents {
    
    private static boolean entered = false;
    private final Map<String, Integer> preClosestEntityIdMap = new HashMap<>();
    private static Thread danMuThread = null;
    public static Queue<DanMu> danMuQueue = new ConcurrentLinkedQueue<>();
    
    @SubscribeEvent
    public void onEnterWorld(PlayerEvent.PlayerLoggedInEvent event) {
        entered = true;
        if (EyeConfig.danMu) {
            DanMuOperations.open();
        }
    }
    
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
        if (entered) {
            Entity entity = e.getEntity();
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null || ((int) entity.getDistance(player)) == 0) {
                return;
            }
            // 实体不为当前玩家同时二者距离在指定半径内
            if (entity.getEntityId() != player.getEntityId() && entity.isEntityInRange(player, EyeConfig.distance)) {
                insideEye(entity, player);
            } else {
                outsideEye();
            }
        }
    }
    
    @SubscribeEvent
    public void onOutWorld(PlayerEvent.PlayerLoggedOutEvent event) {
        entered = false;
        if (danMuThread != null) {
            danMuThread.interrupt();
            danMuThread = null;
        }
        DanMuOperations.close();
        Eye.tips.clear();
        Eye.tipTimes.clear();
        Eye.shouldWarn = false;
        preClosestEntityIdMap.clear();
        
        danMuQueue.clear();
        SendDanMuEvent.TEXT_CACHE.clear();
        DanMuRenderThread.danMuQueue.clear();
    }
    
    @SubscribeEvent
    public void onDanMuRender(RenderGameOverlayEvent e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.ALL && EyeConfig.danMuScroll) {
            if (danMuThread == null && entered) {
                danMuThread = new DanMuRenderThread();
                danMuThread.start();
            }
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            for (DanMu danMu : danMuQueue) {
                int y = (danMu.getLayer() - 1) * (fontRenderer.FONT_HEIGHT + EyeConfig.danMuRowSpacing) + EyeConfig.danMuRowSpacing;
                if (EyeConfig.blackBelt) {
                    y += HudEvents.BLACK_BELT_HEIGHT;
                }
                fontRenderer.drawStringWithShadow(
                        e.getMatrixStack(), danMu.getText(),
                        danMu.getX(), y,
                        Color.WHITE.getRGB());
            }
        }
    }
    
    /**
     * 在检测距离之外
     */
    private void outsideEye() {
        // 当前未检测到实体则判断是否可移除过期提示
        for (String tipType : Eye.tipTimes.keySet()) {
            if (System.currentTimeMillis() - Eye.tipTimes.get(tipType) >= EyeConfig.DISAPPEAR_TIME) {
                Eye.tips.remove(tipType);
                Eye.tipTimes.remove(tipType);
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
            shouldUpdate(EyeConfig.TERRIBLE, entity, player);
        } else {
            shouldUpdate(EyeConfig.OTHER, entity, player);
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
        Integer preClosestEntityId = preClosestEntityIdMap.get(tipType);
        Entity preClosestEntity = null;
        if (Minecraft.getInstance().world != null && preClosestEntityId != null) {
            preClosestEntity = Minecraft.getInstance().world.getEntityByID(preClosestEntityId);
        }
        if (preClosestEntityId == null || preClosestEntity == null ||
                entity.getDistance(player) < preClosestEntity.getDistance(player) || entityId == preClosestEntityId) {
            Eye.tips.put(tipType, getTip(player, entity));
            preClosestEntityIdMap.put(tipType, entityId);
            updateTipTimes(tipType);
            if (tipType.equals(EyeConfig.TERRIBLE)) {
                Eye.shouldWarn = entity.getDistance(player) <= EyeConfig.warnDistance;
            }
        }
    }
    
    /**
     * 更新提示触发时间
     *
     * @param tipType 提示种类
     */
    private void updateTipTimes(String tipType) {
        Long preTime = Eye.tipTimes.get(tipType);
        if (preTime == null || System.currentTimeMillis() - preTime < EyeConfig.DISAPPEAR_TIME) {
            Eye.tipTimes.put(tipType, System.currentTimeMillis());
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
    
}
