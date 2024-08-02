package indi.wzq.BBQBot.plugin.group;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.group.GroupInfo;
import indi.wzq.BBQBot.entity.group.GroupTask;
import indi.wzq.BBQBot.entity.group.UserInfo;
import indi.wzq.BBQBot.enums.Codes;
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

    private static final UserInfoRepository userInfoRepository = SpringUtils.getBean(UserInfoRepository.class);

    private static final GroupInfoRepository groupInfoRepository = SpringUtils.getBean(GroupInfoRepository.class);

    private static final GroupTaskRepository groupTaskRepository = SpringUtils.getBean(GroupTaskRepository.class);

    /**
     * 用户签到事件
     * @param bot Bot
     * @param event Event
     */
    public static void signIn(Bot bot, AnyMessageEvent event) {

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        // 获取签到用户id
        Long signUserId = event.getUserId();

        // 通过签到用户id获取用户信息
        UserInfo userInfo = userInfoRepository.findByUserId(signUserId);

        // 如果未获得到用户信息则初始化用户信息
        if (userInfo == null)
            userInfo = new UserInfo(event.getUserId());

        // 获取当前时间
        Date signInTime = new Date();
        String[] msgs = DailyMaster.getSignInMsg(userInfo, event.getSender().getNickname(), signInTime, event.getMessageId());

        // 返回信息
        for (String msg : msgs)
            bot.sendMsg( event,msg,false );
    }

    /**
     * 今日运势事件
     * @param bot Bot
     * @param event Event
     */
    public static void Fortune(Bot bot, AnyMessageEvent event){
        Random random = new Random();
        String path = "/static/img/jrys/"+(random.nextInt(33)+1)+".jpg";
        // 绘制签到图像
        BufferedImage bufferedImage = GraphicUtils
                .graphicFortune(path);

        Date date = new Date();

        UserInfo userInfo = userInfoRepository.findByUserId(event.getUserId());

        // 如果未获得到用户信息则初始化用户信息
        if (userInfo == null)
            userInfo = new UserInfo(event.getUserId());

        if (DateUtils.isYesterdayOrEarlier(userInfo.getFortuneTime(),date)) {
            String msg = Msg.builder()
                    .at(event.getUserId())
                    .text(" 今日运势")
                    .imgBase64(FileUtils.bufferedImage2Bytes(bufferedImage))
                    .build();

            bot.sendMsg(event, msg, false);
        } else {
            String msg = Msg.builder()
                    .at(event.getUserId())
                    .text(" 每人一天限抽签1次呢！\r\n")
                    .text("贪心的人是不会有好运的。")
                    .build();

            bot.sendMsg(event, msg, false);
        }

        userInfo.setFortuneTime(date);
        userInfoRepository.save(userInfo);
    }

    /**
     * 订阅每日早报事件
     * @param bot Bot
     * @param event Event
     */
    public static void subscribeDailyNews(Bot bot, AnyMessageEvent event) {
        long group_id = event.getGroupId();
        long bot_id = bot.getLoginInfo().getData().getUserId();

        GroupTask groupTask = groupTaskRepository.findByGroupId(group_id);

        if (groupTask == null)
            groupTask = new GroupTask(group_id);

        groupTask.setDailyNews(true);

        groupTaskRepository.save(groupTask);

        GroupInfo groupInfo = groupInfoRepository.findGroupInfoByGroupIdAndBotId(group_id, bot_id);

        if (groupInfo == null) {
            groupInfo =new GroupInfo(group_id,bot_id);
        }
        groupInfoRepository.save(groupInfo);

        String msg = Msg.builder()
                .text("每日早报-订阅成功")
                .build();

        bot.sendMsg(event, msg, false);
    }

    /**
     * 今日早报事件
     * @param bot Bot
     * @param event Event
     */
    public static void DailyNews(Bot bot, AnyMessageEvent event) {
        HttpUtils.Body body = HttpUtils.sendGet("http://dwz.2xb.cn/zaob");

        String url = JSONObject.parseObject(body.getBody()).getString("imageUrl");

        String msg = Msg.builder()
                .img(url)
                .build();

        bot.sendMsg(event, msg, false);
    }

    /**
     * 抽取塔罗牌事件
     * @param bot Bot
     * @param event Event
     */
    public static void getTarot(Bot bot, AnyMessageEvent event){

        bot.sendMsg(event,"少女祷告中...",false);

        // 获取牌数据
        String[] tarot = TarotMaster.getTarots(1)[0];

        // 构建返回信息
        List<String> msgList = TarotMaster.creatMsg(tarot);

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
        String[][] tarots = TarotMaster.getTarots(num);

        // 构建返回信息
        List<String> msgList = TarotMaster.creatMsg(tarots, num);

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

        List<String> msgList = TarotMaster.getFormations(event.getSender().getNickname(), name);

        List<Map<String, Object>> forwardMsg = ShiroUtils
                .generateForwardMsg(
                        bot.getSelfId(),
                        bot.getLoginInfo().getData().getNickname(),
                        msgList);

        // 发送合并转发内容到群（groupId为要发送的群）
        bot.sendGroupForwardMsg(event.getGroupId(), forwardMsg);
    }

    //TODO :错误情况返回信息
}
