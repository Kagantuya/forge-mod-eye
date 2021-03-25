package com.fudansteam.danmu.utils;

import com.fudansteam.Eye;
import com.fudansteam.danmu.site.Site;
import com.fudansteam.danmu.webSocket.WebSocketClient;

/**
 * @author Kagantuya
 */
public class DanMuOperations {
    
    public static void open() {
        Site site = new Site();
        Eye.webSocketClient = new WebSocketClient(site);
        try {
            Eye.webSocketClient.open();
        } catch (Exception e) {
            Eye.webSocketClient = null;
            e.printStackTrace();
        }
    }
    
    public static void close() {
        if (Eye.webSocketClient == null) {
            return;
        }
        try {
            Eye.webSocketClient.close();
        } catch (Exception ignore) {
        } finally {
            Eye.webSocketClient = null;
        }
        Eye.OriginDanMuQueue.clear();
        Eye.CanRenderDanMuQueue.clear();
    }
    
}
