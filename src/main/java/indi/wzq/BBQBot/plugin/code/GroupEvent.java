package indi.wzq.BBQBot.plugin.code;

import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import indi.wzq.BBQBot.plugin.core.*;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static void unPermissions(Bot bot, AnyMessageEvent event){

        String msg = Msg.builder()
                .reply(event.getMessageId())
                .at(event.getUserId())
                .text("\r\n权限不足")
                .text("\r\n请联系管理员使用")
                .build();

        bot.sendMsg( event , msg ,false );

    }

    public static void Help(Bot bot, AnyMessageEvent event){
        List<String> msgList = new ArrayList<>();
        msgList.add(SignCore.getHelp());
        msgList.add(FortuneCore.getHelp());
        msgList.add(TarotCore.getHelp());
        msgList.add(NewsCore.getHelp());
        msgList.add(LiveCore.getHelp());
        msgList.add(UpCore.getHelp());

        List<Map<String, Object>> forwardMsg = ShiroUtils
                .generateForwardMsg(
                        bot.getSelfId(),
                        bot.getLoginInfo().getData().getNickname(),
                        msgList);

        // 发送合并转发内容到群（groupId为要发送的群）
        bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
    }
}
