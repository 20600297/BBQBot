package indi.wzq.BBQBot.enums;

import lombok.Getter;

@Getter
public enum API {
    BILIBILI_QRCODE_GENERATE("https://passport.bilibili.com/x/passport-login/web/qrcode/generate"),
    BILIBILI_QRCODE_POLL("https://passport.bilibili.com/x/passport-login/web/qrcode/poll"),
    ;

    private final String url;

    API(String url) {
        this.url = url;
    }

}
