package com.fudansteam.danmu.site;

import com.fudansteam.danmu.Config;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * @author Kagantuya
 */
public class MessageDeserializer implements JsonDeserializer<String> {
    
    private final Config config;
    
    public MessageDeserializer(Config config) {
        this.config = config;
    }
    
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject data = json.getAsJsonObject();
            String type = data.get("cmd").getAsString();
            switch (type) {
                case "DANMU_MSG":
                    return handleDanMu(data);
                case "SEND_GIFT":
                    return handleGift(data);
                case "COMBO_SEND":
                    return handleComboGift(data);
                case "INTERACT_WORD":
                    return handleNormalEnter(data);
                case "WELCOME":
                    return handleWelcome(data);
                case "WELCOME_GUARD":
                    return handleGuardWelcome(data);
                case "GUARD_BUY":
                    return handleBuyGuard(data);
                case "SUPER_CHAT_MESSAGE":
                    return handleSuperChat(data);
                default:
            }
        }
        return null;
    }
    
    private String handleDanMu(JsonObject dataIn) {
        JsonArray info = dataIn.getAsJsonArray("info");
        JsonArray user = info.get(2).getAsJsonArray();
        String userName = user.get(1).getAsString();
        String danMu = info.get(1).getAsString();
        boolean isAdmin = user.get(2).getAsInt() == 1;
        boolean isGuard = StringUtils.isNotBlank(user.get(7).getAsString());
        if (isAdmin) {
            return String.format(config.getDanMu().getAdminStyleFormatted(), userName, danMu);
        }
        if (isGuard) {
            return String.format(config.getDanMu().getGuardStyleFormatted(), userName, danMu);
        }
        return String.format(config.getDanMu().getNormalStyleFormatted(), userName, danMu);
    }
    
    private String handleGift(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.get("uname").getAsString();
        String action = data.get("action").getAsString();
        String giftName = data.get("giftName").getAsString();
        int num = data.get("num").getAsInt();
        return String.format(config.getGift().getStyleFormatted(), userName, action, giftName, num);
    }
    
    private String handleComboGift(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.get("uname").getAsString();
        String action = data.get("action").getAsString();
        String giftName = data.get("gift_name").getAsString();
        int num = data.get("total_num").getAsInt();
        return String.format(config.getGift().getStyleFormatted(), userName, action, giftName, num);
    }
    
    private String handleNormalEnter(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.get("uname").getAsString();
        return String.format(config.getEnter().getNormalStyleFormatted(), userName);
    }
    
    private String handleWelcome(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.get("uname").getAsString();
        return String.format(config.getEnter().getNormalStyleFormatted(), userName);
    }
    
    private String handleGuardWelcome(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.get("username").getAsString();
        switch (data.get("guard_level").getAsInt()) {
            case 1:
                return String.format(config.getEnter().getGuardStyle1Formatted(), userName);
            case 2:
                return String.format(config.getEnter().getGuardStyle2Formatted(), userName);
            case 3:
                return String.format(config.getEnter().getGuardStyle3Formatted(), userName);
            default:
                return null;
        }
    }
    
    private String handleBuyGuard(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.get("username").getAsString();
        switch (data.get("guard_level").getAsInt()) {
            case 1:
                return String.format(config.getGuard().getGuardStyle1Formatted(), userName);
            case 2:
                return String.format(config.getGuard().getGuardStyle2Formatted(), userName);
            case 3:
                return String.format(config.getGuard().getGuardStyle3Formatted(), userName);
            default:
                return null;
        }
    }
    
    private String handleSuperChat(JsonObject dataIn) {
        JsonObject data = dataIn.getAsJsonObject("data");
        String userName = data.getAsJsonObject("user_info").get("uname").getAsString();
        String message = data.get("message").getAsString();
        int price = data.get("price").getAsInt();
        return String.format(config.getSc().getStyleFormatted(), userName, message, price);
    }
    
}
