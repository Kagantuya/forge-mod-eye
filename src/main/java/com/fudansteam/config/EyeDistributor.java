package com.fudansteam.config;

import com.fudansteam.Eye;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-03 08:08:10
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class EyeDistributor {
    
    private static final File CONFIG_FILE = Minecraft.getInstance().gameDir.toPath().resolve("config").resolve(Eye.ID + ".json").toFile();
    private static final Gson GSON = new Gson();
    
    public static void save() {
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.createNewFile();
            }
            FileUtils.write(CONFIG_FILE, GSON.toJson(EyeConfig.instance), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void load() {
        if (CONFIG_FILE.exists()) {
            try {
                String config = FileUtils.readFileToString(CONFIG_FILE, StandardCharsets.UTF_8);
                EyeConfig.instance = GSON.fromJson(config, EyeConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            EyeConfig.instance = new EyeConfig();
        }
    }
    
}
