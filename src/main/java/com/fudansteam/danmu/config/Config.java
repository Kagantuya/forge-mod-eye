package com.fudansteam.danmu;

import java.util.regex.Matcher;

/**
 * @author Kagantuya
 */
public class Config {
    
    private final DanMu danMu = new DanMu();
    private final Gift gift = new Gift();
    private final Enter enter = new Enter();
    private final Guard guard = new Guard();
    private final SpecialChat sc = new SpecialChat();
    
    public DanMu getDanMu() {
        return danMu;
    }
    
    public Gift getGift() {
        return gift;
    }
    
    public Enter getEnter() {
        return enter;
    }
    
    public Guard getGuard() {
        return guard;
    }
    
    public SpecialChat getSc() {
        return sc;
    }
    
    public static class DanMu {
        
        public String getNormalStyleFormatted() {
            return "&7[普] &b<%{user}> &f%{danMu}".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
                    .replaceAll("%\\{danMu}", Matcher.quoteReplacement("%2$s"));
        }
        
        public String getGuardStyleFormatted() {
            return "&6[舰] &2<%{user}> &f%{danMu}".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
                    .replaceAll("%\\{danMu}", Matcher.quoteReplacement("%2$s"));
        }
        
        public String getAdminStyleFormatted() {
            return "&4[房] &d<%{user}> &f%{danMu}".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
                    .replaceAll("%\\{danMu}", Matcher.quoteReplacement("%2$s"));
        }
        
    }
    
    public static class Gift {
        
        public String getStyleFormatted() {
            return "&7%{user} %{action} [%{gift}] × %{num}".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
                    .replaceAll("%\\{action}", Matcher.quoteReplacement("%2$s"))
                    .replaceAll("%\\{gift}", Matcher.quoteReplacement("%3$s"))
                    .replaceAll("%\\{num}", Matcher.quoteReplacement("%4$s"));
        }
        
    }
    
    public static class Enter {
        
        public String getNormalStyleFormatted() {
            return "&7欢迎 %{user} 进入直播间".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
        public String getGuardStyle1Formatted() {
            return "&4欢迎总督 %{user} 进入直播间".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
        public String getGuardStyle2Formatted() {
            return "&6欢迎提督 %{user} 进入直播间".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
        public String getGuardStyle3Formatted() {
            return "&3欢迎舰长 %{user} 进入直播间".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
    }
    
    public static class Guard {
        
        public String getGuardStyle1Formatted() {
            return "&4%{user} 开通了主播的总督".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
        public String getGuardStyle2Formatted() {
            return "&6%{user} 开通了主播的提督".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
        public String getGuardStyle3Formatted() {
            return "&3%{user} 开通了主播的舰长".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"));
        }
        
    }
    
    public static class SpecialChat {
        
        public String getStyleFormatted() {
            return "&4%{user} > %{msg} [¥%{price}]".replaceAll("&([0-9a-fk-or])", "§$1")
                    .replaceAll("%\\{user}", Matcher.quoteReplacement("%1$s"))
                    .replaceAll("%\\{msg}", Matcher.quoteReplacement("%2$s"))
                    .replaceAll("%\\{price}", Matcher.quoteReplacement("%3$s"));
        }
        
    }
    
}
