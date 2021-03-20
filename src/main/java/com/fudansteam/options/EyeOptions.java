package com.fudansteam.options;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.config.EyeDistributor;
import com.fudansteam.screen.DanMuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-02-28 08:38:17
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class EyeOptions {
    
    private static final FontRenderer TEXT_RENDERER = Minecraft.getInstance().fontRenderer;
    private static final ITextComponent SUPER_EYE_OPEN = new TranslationTextComponent("eye.options.super_eye.open");
    private static final ITextComponent SUPER_EYE_OFF = new TranslationTextComponent("eye.options.super_eye.off");
    
    private static final ITextComponent BLACK_BELT_OPEN = new TranslationTextComponent("eye.options.black_belt.open");
    private static final ITextComponent BLACK_BELT_OFF = new TranslationTextComponent("eye.options.black_belt.off");
    
    public static final SliderPercentageOption EYE_DISTANCE = new SliderPercentageOption("eye.options.distance",
            1.0D, 50.0D, 1.0F,
            gameOptions -> (double) EyeConfig.distance,
            (gameOptions, distance) -> {
                EyeConfig.distance = distance.intValue();
                EyeDistributor.save();
            },
            (gameOptions, option) -> new TranslationTextComponent("eye.options.distance_s", EyeConfig.distance));
    
    public static final SliderPercentageOption WARN_DISTANCE = new SliderPercentageOption("eye.options.warn_distance",
            1.0D, 50.0D, 1.0F,
            gameOptions -> (double) EyeConfig.warnDistance,
            (gameOptions, warnDistance) -> {
                EyeConfig.warnDistance = warnDistance.intValue();
                EyeDistributor.save();
            },
            (gameOptions, option) -> new TranslationTextComponent("eye.options.warn_distance_s", EyeConfig.warnDistance));
    
    public static final IteratableOption SUPER_EYE = new IteratableOption("eye.options.super_eye",
            (gameOptions, integer) -> {
                EyeConfig.superEye = !EyeConfig.superEye;
                if (EyeConfig.superEye) {
                    Eye.originGamma = gameOptions.gamma;
                    gameOptions.gamma = 100;
                } else if (Eye.originGamma != -1) {
                    gameOptions.gamma = Eye.originGamma;
                }
                EyeDistributor.save();
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(TEXT_RENDERER.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.super_eye"), 200));
                return EyeConfig.superEye ? SUPER_EYE_OPEN : SUPER_EYE_OFF;
            });
    
    public static final IteratableOption BLACK_BELT = new IteratableOption("eye.options.black_belt",
            (gameOptions, integer) -> {
                EyeConfig.blackBelt = !EyeConfig.blackBelt;
                EyeDistributor.save();
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(TEXT_RENDERER.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.black_belt"), 200));
                return EyeConfig.blackBelt ? BLACK_BELT_OPEN : BLACK_BELT_OFF;
            });
    
    public static final IteratableOption DAN_MU = new IteratableOption("eye.options.dan_mu",
            (gameOptions, integer) -> Minecraft.getInstance().displayGuiScreen(new DanMuScreen(Minecraft.getInstance().currentScreen)),
            (gameOptions, cyclingOption) -> new TranslationTextComponent("eye.options.dan_mu"));
    
}