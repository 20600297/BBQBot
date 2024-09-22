package indi.wzq.BBQBot.plugin.code;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.group.GroupInfo;
import indi.wzq.BBQBot.entity.group.GroupTask;
import indi.wzq.BBQBot.entity.group.UserInfo;
import indi.wzq.BBQBot.enums.Codes;
import indi.wzq.BBQBot.plugin.core.FortuneCore;
import indi.wzq.BBQBot.plugin.core.NewsCore;
import indi.wzq.BBQBot.plugin.core.SignCore;
import indi.wzq.BBQBot.plugin.core.TarotCore;
import indi.wzq.BBQBot.repo.GroupInfoRepository;
import indi.wzq.BBQBot.repo.GroupTaskRepository;
import indi.wzq.BBQBot.repo.UserInfoRepository;
import indi.wzq.BBQBot.utils.DateUtils;
import indi.wzq.BBQBot.utils.FileUtils;
import indi.wzq.BBQBot.utils.Graphic.GraphicUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.http.HttpUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;
import lombok.extern.slf4j.Slf4j;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GroupCodes {

    /**
     * 用户签到事件
     * @param bot Bot
     * @param event Event
     */
    public static void signIn(Bot bot, AnyMessageEvent event) {

        String[] msgs = SignCore.getSignInMsg(event.getUserId(), event.getSender().getNickname(), event.getMessageId());

        // 返回信息
        for (String msg : msgs)
            bot.sendMsg( event,msg,false );

    }

    /**
     * 今日运势事件
     * @param bot Bot
     * @param event Event
     */
    public static void fortune(Bot bot, AnyMessageEvent event){

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        bot.sendMsg(event , FortuneCore.getFortune(event.getUserId()),false);

    }

    /**
     * 订阅每日早报事件
     * @param bot Bot
     * @param event Event
     */
    public static void subscribeDailyNews(Bot bot, AnyMessageEvent event) {

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        bot.sendMsg(event,
                NewsCore.subscribe(event.getGroupId(),bot.getSelfId()),
                false);

    }

    /**
     * 今日早报事件
     * @param bot Bot
     * @param event Event
     */
    public static void todayNews(Bot bot, AnyMessageEvent event) {

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        bot.sendMsg(event, NewsCore.todayNews() , false);
    }

    /**
     * 抽取塔罗牌事件
     * @param bot Bot
     * @param event Event
     */
    public static void getTarot(Bot bot, AnyMessageEvent event){

        bot.sendMsg(event,"少女祷告中...",false);

        // 获取牌数据
        String[] tarot = TarotCore.getTarots(1)[0];

        // 构建返回信息
        List<String> msgList = TarotCore.creatMsg(tarot);

        // 构建合并转发消息（selfId为合并转发消息显示的账号，nickname为显示的发送者昵称，msgList为消息列表）
        List<Map<String, Object>> forwardMsg = ShiroUtils
                .generateForwardMsg(
                        bot.getSelfId(),
                        bot.getLoginInfo().getData().getNickname(),
                        msgList);

        // 发送合并转发内容到群（groupId为要发送的群）
        bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
    }

    /**
     * 抽取多张塔罗牌事件
     * @param bot Bot
     * @param event Event
     */
    public static void getTarots(Bot bot, AnyMessageEvent event){
        bot.sendMsg(event,"少女祷告中...",false);


        Pattern p = Pattern.compile(Codes.TAROT_GET_TAROTS.getStr());
        Matcher m = p.matcher(event.getRawMessage().trim());
        if ( !m.matches()) {
            log.warn("参数截取异常！");
            return;
        }

        int num = Integer.parseInt(m.group(1));

        if (num > 10) {
            String msg = Msg.builder()
                    .reply(event.getMessageId())
                    .text("数量太多了，不给抽！")
                    .build();
            bot.sendMsg(event, msg, false);
            return;
        }

        // 获取牌数据
        String[][] tarots = TarotCore.getTarots(num);

        // 构建返回信息
        List<String> msgList = TarotCore.creatMsg(tarots, num);

        // 构建合并转发消息（selfId为合并转发消息显示的账号，nickname为显示的发送者昵称，msgList为消息列表）
        List<Map<String, Object>> forwardMsg = ShiroUtils
                .generateForwardMsg(
                        bot.getSelfId(),
                        bot.getLoginInfo().getData().getNickname(),
                        msgList);

        // 发送合并转发内容到群（groupId为要发送的群）
        bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
    }

    /**
     * 塔罗牌阵事件
     * @param bot Bot
     * @param event Event
     */
    public static void getFormations(Bot bot, AnyMessageEvent event){
        bot.sendMsg(event,"少女祷告中...",false);
        String name = event.getMessage().replaceAll("^塔罗牌阵", "").trim() + "牌阵";

        List<String> msgList = TarotCore.getFormations(event.getSender().getNickname(), name);

        List<Map<String, Object>> forwardMsg = ShiroUtils
                .generateForwardMsg(
                        bot.getSelfId(),
                        bot.getLoginInfo().getData().getNickname(),
                        msgList);

        // 发送合并转发内容到群（groupId为要发送的群）
        bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
    }

}
