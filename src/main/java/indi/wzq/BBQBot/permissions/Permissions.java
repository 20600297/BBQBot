package indi.wzq.BBQBot.permissions;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.enums.PermissionsEnums;

public class Permissions {

    /**
     * 检查用户权限
     *
     * @return PermissionsEnums
     */
    public static PermissionsEnums checkAdmin(Bot bot, AnyMessageEvent event) {
        if (isAdmin(bot, event)) {
            return PermissionsEnums.ADMIN;
        } else {
            return PermissionsEnums.USER;
        }
    }

    /**
     * 判断用户是否是管理员或者群主 或系统管理员
     *
     * @param bot   bot
     * @param event event
     * @return true 是管理员或群主
     */
    private static boolean isAdmin(Bot bot, AnyMessageEvent event) {
        String role;
        try {
            role = bot.getGroupMemberInfo(event.getGroupId(), event.getUserId(), true).getData().getRole();
        } catch (Exception e) {
            return false;
        }
        if (role == null || role.isEmpty()) {
            return false;
        }
        return role.equals("owner") || role.equals("admin");
    }

}
