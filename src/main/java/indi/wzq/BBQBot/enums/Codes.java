package indi.wzq.BBQBot.enums;

import lombok.Getter;

/**
 * 指令 枚举
 */
@Getter
public enum Codes {
    LIVE_SUBSCRIBE("^订阅直播间"),
    GROUP_SIGNIN("^签到"),
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
