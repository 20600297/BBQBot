package indi.wzq.BBQBot.enums;

import lombok.Getter;

@Getter
public enum PermissionsEnums {
    //普通用户
    USER("普通用户", 1),
    //管理员用户
    ADMIN("管理员用户", 2),
    //后台用户
    MANAGE("后台用户", 3),
    ;

    private final String str;
    private final int i;

    PermissionsEnums(String s, int i) {
        this.str = s;
        this.i = i;
    }
}
