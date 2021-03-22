package com.fudansteam.events;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.utils.HttpUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-21 12:17:33
 * Copyright 2021 HDU_IES. All rights reserved.
 */
@Mod.EventBusSubscriber
public class CommandEvents {
    
    @SubscribeEvent
    public static void onServerStaring(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(Eye.ID).requires((context) -> context.hasPermissionLevel(0))
                .then(Commands.literal("sendBiliLive")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes((context) -> {
                                    if (Eye.loginCookies == null) {
                                        context.getSource().sendFeedback(new TranslationTextComponent("eye.command.inform.login"), false);
                                        return 0;
                                    } else {
                                        String message = StringArgumentType.getString(context, "message");
                                        Map<String, String> params = new HashMap<>(9);
                                        params.put("fontsize", "25");
                                        params.put("color", "16777215");
                                        params.put("mode", "1");
                                        params.put("msg", message);
                                        params.put("rnd", String.valueOf(System.currentTimeMillis()));
                                        params.put("roomid", EyeConfig.roomId);
                                        params.put("bubble", "0");
                                        params.put("csrf_token", Eye.loginCookies.get("SESSDATA"));
                                        params.put("csrf", Eye.loginCookies.get("bili_jct"));
                                        Map<String, String> headers = new HashMap<>(4);
                                        headers.put("cookie", Eye.loginCookies.get("FinalCookie"));
                                        headers.put("origin", "https://live.bilibili.com");
                                        headers.put("referer", "https://live.bilibili.com/blanc/1029?liteVersion=true");
                                        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
                                        try {
                                            HttpUtil.sendPostForm("https://api.live.bilibili.com/msg/send", params, headers);
                                        } catch (Exception e) {
                                            context.getSource().sendFeedback(new TranslationTextComponent("eye.command.inform.live_send.fail"), false);
                                            e.printStackTrace();
                                        }
                                        context.getSource().sendFeedback(new TranslationTextComponent("eye.command.inform.live_send.suc", message), false);
                                    }
                                    return 1;
                                }))));
    }
    
}
