package indi.wzq.BBQBot.plugin.group;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import indi.wzq.BBQBot.utils.onebot.Msg;

public class GroupEvent {

    public static void ConsentAdd(Bot bot, GroupAddRequestEvent event){
        String bName = event.getComment();
        if (bName.equals("")){
            bot.setGroupAddRequest(event.getFlag(),event.getSubType(),true,"");

            String msg = Msg.builder()
                    .at(event.getUserId())
                    .text(" 这里是momo的粮仓！" + "\t\n")
                    .text("——————————" + "\t\n")
                    .text("您没有填B站昵称？" + "\t\n")
                    .text("真是个神密的人呢" + "\t\n")
                    .text("——————————" + "\t\n")
                    .text("欢迎您的加入！")
                    .build();
            bot.sendGroupMsg(event.getGroupId(), msg,false);

        } else {
            bot.setGroupAddRequest(event.getFlag(),event.getSubType(),true,"");

            String msg = Msg.builder()
                    .at(event.getUserId())
                    .text(" 这里是momo的粮仓！" + "\t\n")
                    .text("——————————" + "\t\n")
                    .text("您的B站昵称：" + "\t\n")
                    .text(event.getComment() + "\t\n")
                    .text("——————————" + "\t\n")
                    .text("欢迎您的加入！")
                    .build();
            bot.sendGroupMsg(event.getGroupId(), msg,false);
        }

    }

    public static void Decrease(Bot bot, GroupDecreaseNoticeEvent event){
        bot.sendGroupMsg(event.getGroupId(), "["+event.getUserId()+"] 离我们而去了！",false);
    }
}
