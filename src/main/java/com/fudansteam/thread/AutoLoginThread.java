package com.fudansteam.thread;

import com.fudansteam.Eye;
import com.fudansteam.screen.LoginScreen;
import com.fudansteam.utils.BiliUtil;

import java.util.Map;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-22 21:04:47
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class AutoLoginThread extends Thread {
    
    @Override
    public void run() {
        try {
            while (true) {
                Map<String, String> map = BiliUtil.tryLogin(Eye.oauthKey);
                if (map.size() != 1) {
                    Eye.loginCookies = map;
                    LoginScreen.confirmed = true;
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
