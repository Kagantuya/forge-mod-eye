package com.fudansteam.events;

import com.fudansteam.screen.EyeScreen;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-16 22:44:59
 * Copyright 2021 HDU_IES. All rights reserved.
 */
@Mod.EventBusSubscriber
public class OptionsScreenEvents {
    
    @SubscribeEvent
    public static void onInit(GuiScreenEvent.InitGuiEvent e) {
        if (e.getGui() instanceof OptionsScreen) {
            Minecraft minecraft = Minecraft.getInstance();
            MainWindow mainWindow = minecraft.getMainWindow();
            e.addWidget(new Button(mainWindow.getScaledWidth() / 2 - 155, mainWindow.getScaledHeight() / 6 + 144 - 6, 150, 20,
                    new TranslationTextComponent("eye.menu"),
                    (button) -> minecraft.displayGuiScreen(new EyeScreen(minecraft.currentScreen))));
        }
    }
    
}
