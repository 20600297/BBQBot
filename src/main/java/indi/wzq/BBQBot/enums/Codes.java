package indi.wzq.BBQBot.enums;

import lombok.Getter;

/**
 * 指令 枚举
 */
@Getter
public enum Codes {
    LIVE_SUBSCRIBE("^订阅直播间"),
    GROUP_SIGNIN("^签到"),
    GROUP_FORTUNE("^今日运势"),
    GROUP_DAILYNEWS("^今日早报"),
    GROUP_SUBSCRIBE_DAILYNEWS("^订阅每日早报"),
    CHAT_AIR_CONDITIONER_OPEN("^空调开"),
    CHAT_AIR_CONDITIONER_CLOSE("^空调关"),
    CHAT_TEMPERATURE("^群温度"),
    CHAT_SET_TEMPERATURE("^设置温度")
    ;

    private String str;

    Codes(String s) {
        str = s;
    }

    public Codes setStr(String str) {
        this.str = str;
        return this;
    }
}
