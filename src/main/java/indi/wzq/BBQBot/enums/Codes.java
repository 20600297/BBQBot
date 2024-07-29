package indi.wzq.BBQBot.enums;

import lombok.Getter;

/**
 * 指令 枚举
 */
@Getter
public enum Codes {
    HELP("^帮助"),

    LIVE_SUBSCRIBE("^订阅直播间"),

    GROUP_SIGNIN("^签到"),
    GROUP_FORTUNE("^今日运势"),
    GROUP_DAILYNEWS("^今日早报"),
    GROUP_SUBSCRIBE_DAILYNEWS("^订阅每日早报"),

    CHAT_AIR_CONDITIONER_OPEN("^空调开"),
    CHAT_AIR_CONDITIONER_CLOSE("^空调关"),

    TAROT_GET_TAROT("^抽塔罗牌"),
    TAROT_GET_TAROTS("^抽([1-9]\\d*)张塔罗牌"),
    TAROT_GET_FORMATIONS("^塔罗牌阵(圣三角|时间之流|四要素|五牌阵|吉普赛十字|马蹄|六芒星)"),
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
