package com.fudansteam.utils;

import com.fudansteam.Eye;
import com.fudansteam.screen.LoginScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-21 17:48:57
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class BiliUtil {
    
    public static void beforeTryLogin() {
        try {
            // 获取 oauthKey
            String qrRes = HttpUtil.sendGet("http://passport.bilibili.com/qrcode/getLoginUrl", null, null);
            JsonObject qrData = new JsonParser().parse(qrRes).getAsJsonObject().get("data").getAsJsonObject();
            Eye.oauthKey = qrData.get("oauthKey").getAsString();
            String qrUrl = "https://api88.net/api/code/?text=" + qrData.get("url").getAsString() + "&type=img";
            LoginScreen.texture = new DynamicTexture(NativeImage.read(HttpUtil.sendPost(qrUrl, null)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String, String> tryLogin(String oauthKey) throws Exception {
        Map<String, String> loginParams = new HashMap<>(2);
        Map<String, String> loginCookies = new HashMap<>(1);
        loginParams.put("oauthKey", oauthKey);
        loginParams.put("gourl", "http://www.bilibili.com");
        String loginRes = HttpUtil.sendPostForm("http://passport.bilibili.com/qrcode/getLoginInfo", loginParams, null);
        System.out.println(loginRes);
        JsonElement loginData = new JsonParser().parse(loginRes).getAsJsonObject().get("data");
        if (loginData.isJsonObject()) {
            String url = loginData.getAsJsonObject().get("url").getAsString();
            loginCookies = HttpUtil.urlParser(url);
            loginCookies.put("FinalCookie", getFinalCookie(loginCookies));
        } else {
            switch (loginData.getAsNumber().intValue()) {
                case -1:
                    loginCookies.put("inform", "eye.inform.qr.error");
                    break;
                case -2:
                    loginCookies.put("inform", "eye.inform.qr.expired");
                    beforeTryLogin();
                    break;
                case -4:
                    loginCookies.put("inform", "eye.inform.qr.not_scanned");
                    break;
                case -5:
                    loginCookies.put("inform", "eye.inform.qr.not_confirm");
                    break;
                default:
            }
        }
        return loginCookies;
    }
    
    public static String getUsername(String cookie) {
        String username = null;
        try {
            Map<String, String> headers = new HashMap<>(1);
            headers.put("cookie", cookie);
            String res = HttpUtil.sendGet("http://api.bilibili.com/x/web-interface/nav", null, headers);
            username = new JsonParser().parse(res).getAsJsonObject().get("data").getAsJsonObject().get("uname").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }
    
    public static void logout() {
        if (Eye.loginCookies == null) {
            return;
        }
        Map<String, String> params = new HashMap<>(2);
        params.put("gourl", "https://account.bilibili.com/account/home");
        params.put("biliCSRF", Eye.loginCookies.get("bili_jct"));
        Map<String, String> headers = new HashMap<>(1);
        headers.put("cookie", Eye.loginCookies.get("FinalCookie"));
        try {
            HttpUtil.sendPostForm("http://passport.bilibili.com/login/exit/v2", params, headers);
            Eye.loginCookies = null;
            Eye.oauthKey = null;
            Eye.username = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getFinalCookie(Map<String, String> cookies) {
        String template = "%s=%s; %s=%s; %s=%s; %s=%s";
        return String.format(template, "SESSDATA", cookies.get("SESSDATA"),
                "bili_jct", cookies.get("bili_jct"),
                "DedeUserID", cookies.get("DedeUserID"),
                "DedeUserID__ckMd5", cookies.get("DedeUserID__ckMd5"));
    }
    
}
