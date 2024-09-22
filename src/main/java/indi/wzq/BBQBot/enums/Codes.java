package indi.wzq.BBQBot.enums;

import lombok.Getter;

/**
 * 指令 枚举
 */
@Getter
public enum Codes {
    HELP("^帮助$", PermissionsEnums.USER),

    SUBSCRIBE_LIVE("^订阅直播间", PermissionsEnums.ADMIN),
    SUBSCRIBE_UP("^订阅UP主", PermissionsEnums.ADMIN),
    SUBSCRIBE_DAILYNEWS("^订阅每日早报$", PermissionsEnums.ADMIN),

    GROUP_SIGNIN("^签到$", PermissionsEnums.USER),
    GROUP_FORTUNE("^今日运势$", PermissionsEnums.USER),
    GROUP_DAILYNEWS("^今日早报$", PermissionsEnums.USER),

    TAROT_GET_TAROT("^抽塔罗牌$", PermissionsEnums.USER),
    TAROT_GET_TAROTS("^抽([1-9]\\d*)张塔罗牌$", PermissionsEnums.USER),
    TAROT_GET_FORMATIONS("^塔罗牌阵(圣三角|时间之流|四要素|五牌阵|吉普赛十字|马蹄|六芒星)$", PermissionsEnums.USER),

    BILIBILI_ACCOUNT_LOGIN("^扫码登录B站账号", PermissionsEnums.MANAGE),
    ;

    private final PermissionsEnums permissions;
    private final String str;

    Codes(String s, PermissionsEnums permissions) {
        str = s;
        this.permissions = permissions;
    }
}
