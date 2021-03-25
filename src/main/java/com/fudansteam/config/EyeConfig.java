package com.fudansteam.config;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-02-27 16:19:37
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class EyeConfig {
    
    public static EyeConfig instance = null;
    
    public int distance;
    public boolean enableSuperEye;
    public int warnDistance;
    public boolean enableBlackBelt;
    public String roomId;
    public boolean enableDanMu;
    public int danMuLayer;
    public boolean enableDanMuScroll;
    public int danMuRowSpacing;
    
    public EyeConfig() {
        distance = 15;
        enableSuperEye = false;
        warnDistance = 5;
        enableBlackBelt = false;
        roomId = "";
        enableDanMu = false;
        danMuLayer = 10;
        enableDanMuScroll = false;
        danMuRowSpacing = 3;
    }
    
}
