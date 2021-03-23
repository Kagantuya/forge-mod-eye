package com.fudansteam.options;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.fudansteam.config.EyeDistributor;
import com.fudansteam.danmu.utils.DanMuOperations;
import com.fudansteam.screen.DanMuScreen;
import com.fudansteam.screen.LoginScreen;
import com.fudansteam.utils.BiliUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
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
            (gameOptions, value) -> {
                EyeConfig.distance = value.intValue();
                EyeDistributor.save();
            },
            (gameOptions, option) -> new TranslationTextComponent("eye.options.distance_s", EyeConfig.distance));
    
    public static final SliderPercentageOption WARN_DISTANCE = new SliderPercentageOption("eye.options.warn_distance",
            1.0D, 50.0D, 1.0F,
            gameOptions -> (double) EyeConfig.warnDistance,
            (gameOptions, value) -> {
                EyeConfig.warnDistance = value.intValue();
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
    
    private static final ITextComponent DAN_MU_OPEN = new TranslationTextComponent("eye.options.dan_mu.open");
    private static final ITextComponent DAN_MU_OFF = new TranslationTextComponent("eye.options.dan_mu.off");
    
    public static final IteratableOption DAN_MU_OPTION = new IteratableOption("eye.options.dan_mu_option",
            (gameOptions, integer) -> {
                EyeConfig.danMu = !EyeConfig.danMu;
                if (Minecraft.getInstance().player != null) {
                    if (EyeConfig.danMu) {
                        DanMuOperations.open();
                    } else {
                        DanMuOperations.close();
                    }
                }
                EyeDistributor.save();
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.dan_mu_option"), 200));
                return EyeConfig.danMu ? DAN_MU_OPEN : DAN_MU_OFF;
            });
    
    public static final IteratableOption BILI_OPTION = new IteratableOption("eye.options.bili_option.login",
            (gameSettings, integer) -> {
                Minecraft instance = Minecraft.getInstance();
                if (Eye.loginCookies == null) {
                    instance.displayGuiScreen(new LoginScreen(Minecraft.getInstance().currentScreen));
                } else {
                    instance.displayGuiScreen(new ConfirmScreen(
                            (flag) -> {
                                if (flag) {
                                    BiliUtil.logout();
                                }
                                instance.displayGuiScreen(DanMuScreen.instance);
                            },
                            new TranslationTextComponent("eye.title.logout"), new TranslationTextComponent("eye.title.logout.sub")
                    ));
                }
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.bili_option"), 200));
                return Eye.loginCookies == null ?
                        new TranslationTextComponent("eye.options.bili_option.login") :
                        new TranslationTextComponent("eye.options.bili_option.logged", Eye.username);
            });
    
    public static final IteratableOption DAN_MU_USAGE = new IteratableOption("eye.options.dan_mu.usage",
            (gameSettings, integer) -> {
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.dan_mu_usage"), 200));
                return new TranslationTextComponent("eye.options.dan_mu.usage");
            });
    
    public static final SliderPercentageOption DAN_MU_LAYER = new SliderPercentageOption("eye.options.dan_mu_layer",
            1.0D, 10.0D, 1.0F,
            gameOptions -> (double) EyeConfig.danMuLayer,
            (gameOptions, value) -> {
                EyeConfig.danMuLayer = value.intValue();
                EyeDistributor.save();
            },
            (gameOptions, option) -> new TranslationTextComponent("eye.options.dan_mu_layer_l", EyeConfig.danMuLayer));
    
    private static final TranslationTextComponent DAN_MU_SCROLL_OPEN = new TranslationTextComponent("eye.options.dan_mu_scroll.open");
    private static final TranslationTextComponent DAN_MU_SCROLL_OFF = new TranslationTextComponent("eye.options.dan_mu_scroll.off");
    
    public static final IteratableOption DAN_MU_SCROLL = new IteratableOption("eye.options.dan_mu_scroll",
            (gameOptions, integer) -> {
                EyeConfig.danMuScroll = !EyeConfig.danMuScroll;
                EyeDistributor.save();
            },
            (gameOptions, cyclingOption) -> {
                cyclingOption.setOptionValues(Minecraft.getInstance().fontRenderer.trimStringToWidth(new TranslationTextComponent("eye.options.tooltip.dan_mu_scroll"), 200));
                return EyeConfig.danMuScroll ? DAN_MU_SCROLL_OPEN : DAN_MU_SCROLL_OFF;
            });
    
    public static final SliderPercentageOption DAN_MU_ROW_SPACING = new SliderPercentageOption("eye.options.dan_mu_row_spacing",
            1.0D, 5.0D, 1.0F,
            gameOptions -> (double) EyeConfig.danMuRowSpacing,
            (gameOptions, value) -> {
                EyeConfig.danMuRowSpacing = value.intValue();
                EyeDistributor.save();
            },
            (gameOptions, option) -> new TranslationTextComponent("eye.options.dan_mu_row_spacing_s", EyeConfig.danMuRowSpacing));
    
}
