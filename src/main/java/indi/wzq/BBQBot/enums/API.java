package indi.wzq.BBQBot.enums;

import lombok.Getter;

@Getter
public enum API {
    /**
     * 获取二维码地址及密匙
     */
    BILIBILI_QRCODE_GENERATE("https://passport.bilibili.com/x/passport-login/web/qrcode/generate"),
    /**
     * 验证二维码状态
     * 参数:qrcode_key - 获取二维码时同步获取的密匙
     */
    BILIBILI_QRCODE_POLL("https://passport.bilibili.com/x/passport-login/web/qrcode/poll"),
    /**
     * 获取用户名片信息 需要Cookies 参数:mid - 用户mid
     */
    BILIBILI_GET_USER_CARD("https://api.bilibili.com/x/web-interface/card"),
    /**
     * 获取用户空间动态 需要Cookies 参数: host_mid - 用户mid
     */
    BILIBILI_GET_DYNAMIC_SPACE("https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/space"),
    /**
     * 获取动态详情 需要Cookies 参数: id - 动态ID
     */
    BILIBILI_GET_DYNAMIC_DETAIL("https://api.bilibili.com/x/polymer/web-dynamic/v1/detail"),
    ;

    private final String url;

    API(String url) {
        this.url = url;
    }

}
