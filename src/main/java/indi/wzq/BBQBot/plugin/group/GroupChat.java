package indi.wzq.BBQBot.plugin.group;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

public class GroupChat {

    public static void AirConditionerOpen(Bot bot, AnyMessageEvent event) {
        bot.sendMsg(event,"❄️哔~",false);
    }

    public static void AirConditionerClose(Bot bot, AnyMessageEvent event) {
        bot.sendMsg(event,"💤哔~",false);
    }

}
