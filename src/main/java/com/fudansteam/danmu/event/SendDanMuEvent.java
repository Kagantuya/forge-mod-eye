package com.fudansteam.danmu.event;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.danmu.entity.DanMu;
import com.fudansteam.thread.ScrollDanMuThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Kagantuya
 */
@Mod.EventBusSubscriber
public class SendDanMuEvent extends Event {
    
    private final String message;
    private static final int DAN_MU_INTERVAL = 10;
    public static final Queue<String> TEXT_CACHE = new ConcurrentLinkedQueue<>();
    
    public SendDanMuEvent(String message) {
        this.message = message;
    }
    
    @SubscribeEvent
    public static void onSendDanMu(SendDanMuEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        // 防止游戏内 hud 未就绪导致弹幕堆积重叠
        if (EyeConfig.danMuScroll && Eye.entered) {
            // 先进缓存
            TEXT_CACHE.add(event.getMessage());
            DanMu danMu = new DanMu();
            danMu.setText(TEXT_CACHE.peek());
            danMu.setX(minecraft.getMainWindow().getScaledWidth());
            int layer = getSuitableLayer();
            if (layer != -1) {
                danMu.setLayer(layer);
                // 给线程提供数据并出队
                ScrollDanMuThread.danMuQueue.add(danMu);
                TEXT_CACHE.poll();
            }
        }
        if (player != null) {
            player.sendMessage(new StringTextComponent(event.getMessage()), player.getUniqueID());
        }
    }
    
    private static int getSuitableLayer() {
        Minecraft instance = Minecraft.getInstance();
        int finalLayer = 1;
        Map<Integer, DanMu> latestDanMus = new HashMap<>();
        // 按层分割现有弹幕，获取每层最后出现的弹幕
        for (DanMu danMu : ScrollDanMuThread.danMuQueue) {
            int currentLayer = danMu.getLayer();
            DanMu latestDanMu = latestDanMus.get(currentLayer);
            if (latestDanMu == null) {
                latestDanMus.put(currentLayer, danMu);
            } else if (danMu.getX() > latestDanMu.getX()) {
                // 当前弹幕在之前弹幕之后则更新 map
                latestDanMus.put(currentLayer, danMu);
            }
        }
        // 每层遍历判断，防止弹幕重叠
        for (int l = 1; l <= EyeConfig.danMuLayer; l++) {
            DanMu danMu = latestDanMus.get(l);
            if (danMu != null) {
                int totalLength = danMu.getX() + instance.fontRenderer.getStringWidth(danMu.getText()) + DAN_MU_INTERVAL;
                // 若弹幕长度超出屏幕则切换至下一行尝试
                if (totalLength > instance.getMainWindow().getScaledWidth()) {
                    // 没有剩余空间则暂存至缓存队列
                    if (l == EyeConfig.danMuLayer) {
                        return -1;
                    }
                    finalLayer++;
                } else {
                    finalLayer = l;
                    break;
                }
            }
        }
        return finalLayer;
    }
    
    public String getMessage() {
        return message;
    }
    
}
