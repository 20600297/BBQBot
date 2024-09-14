package indi.wzq.BBQBot.plugin.group;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import indi.wzq.BBQBot.utils.onebot.Msg;

public class GroupEvent {

    public static void ConsentAdd(Bot bot, GroupAddRequestEvent event){
        bot.setGroupAddRequest(event.getFlag(),event.getSubType(),true,"");

        String msg = Msg.builder()
                .at(event.getUserId())
                .text("\t\n")
                .text("欢迎您的加入！")
                .build();
        bot.sendGroupMsg(event.getGroupId(), msg,false);

    }

    public static void Decrease(Bot bot, GroupDecreaseNoticeEvent event){
        bot.sendGroupMsg(event.getGroupId(), "["+event.getUserId()+"] 离我们而去了！",false);
    }
}
