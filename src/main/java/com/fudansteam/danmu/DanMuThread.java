package com.fudansteam.danmu;

import com.fudansteam.Eye;
import com.fudansteam.config.EyeConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-19 08:47:45
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class DanMuThread extends Thread {
    
    private final Set<String> preRnd = new HashSet<>();
    private final Set<String> preTimeline = new HashSet<>();
    private final Set<String> danMu = new HashSet<>();
    private int preRndCount = 0;
    
    public static String sendGet(String url) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (X11; Linux x86_64; rv:87.0) Gecko/20100101 Firefox/87.0");
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
    
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            if (EyeConfig.roomId.length() == 0) {
                continue;
            }
            String res = sendGet("https://api.live.bilibili.com/xlive/web-room/v1/dM/gethistory?roomid=" + EyeConfig.roomId);
            JsonArray danMus = new JsonParser().parse(res).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("room");
            if (danMus.size() == 0) {
                continue;
            }
            for (JsonElement element : danMus) {
                JsonObject item = element.getAsJsonObject();
                String rnd = item.get("rnd").getAsString();
                String timeline = item.get("timeline").getAsString();
                String danMu = item.get("text").getAsString();
                if (!this.preRnd.contains(rnd) || !this.preTimeline.contains(timeline) || !this.danMu.contains(danMu)) {
                    Eye.danMu.add("[" + item.get("nickname").getAsString() + "]: " + danMu);
                    this.preRnd.add(rnd);
                    this.preTimeline.add(timeline);
                    this.danMu.add(danMu);
                    this.preRndCount++;
                } else if (this.preRndCount == 20) {
                    // 由于接口返回仅十条弹幕，稍等一会确保缓存完全失效（不对上方新来的弹幕判断造成影响）后清除
                    this.preRnd.clear();
                    this.preTimeline.clear();
                    this.danMu.clear();
                    this.preRndCount = 0;
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
