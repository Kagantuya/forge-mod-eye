package com.fudansteam.danmu.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Kagantuya
 */
@Mod.EventBusSubscriber
public class SendDanMuEvent extends Event {
    
    private final String message;
    
    public SendDanMuEvent(String message) {
        this.message = message;
    }
    
    @SubscribeEvent
    public static void onSendDanMu(SendDanMuEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        if (player != null) {
            player.sendMessage(new StringTextComponent(event.getMessage()), player.getUniqueID());
        }
    }
    
    public String getMessage() {
        return message;
    }
    
}
