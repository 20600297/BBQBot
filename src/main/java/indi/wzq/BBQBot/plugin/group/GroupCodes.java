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
import okhttp3.Headers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

        // 获取当前时间
        Date signInTime = new Date();

        // 判断是否为初次签到
        if (userInfo == null) {

            userInfo = creatUserInfo(signUserId);
            // 初始化用户信息
            userInfo.setSignInTime(signInTime);
            userInfo.setSignInNum(1);
            userInfo.setSignInContNum(1);
            userInfoRepository.save(userInfo);

            sendSignMsg(bot,event,userInfo);

        } else {

            // 判断今日是否签到
            if (DateUtils.isSameDay(signInTime,userInfo.getSignInTime())){

                // 构建消息
                String msg = Msg.builder().reply(event.getMessageId())
                        .at(signUserId)
                        .text("您今天已经签过到了呢！")
                        .build();

                // 发送信息
                bot.sendMsg(event, msg, false);

            } else {

                // 判断是否为连续签到
                if (DateUtils.isYesterdayOrEarlier(userInfo.getSignInTime(),signInTime)){
                    userInfo.setSignInContNum(userInfo.getSignInContNum() + 1);
                } else {
                    userInfo.setSignInContNum(1);
                }

                userInfo.setSignInTime(signInTime);

                userInfo.setSignInNum(userInfo.getSignInNum() + 1);

                // 更新用户信息
                userInfoRepository.save(userInfo);

                sendSignMsg(bot,event,userInfo);
            }
        }
    }

    /**
     * 发送签到成功消息
     * @param bot Bot
     * @param event Event
     * @param user_info 用户信息
     */
    private static void sendSignMsg(Bot bot, AnyMessageEvent event,UserInfo user_info) {
        // 绘制签到图像
        BufferedImage bufferedImage = GraphicUtils
                .graphicSignInMsg(getBackground(), getQqFace(user_info.getUserId()), event.getSender().getNickname());

        String directoryPath = "./data/img/signInMsg/";
        String fileName = DateUtils.format(new Date(), "yyyy-MM-dd") +"-" + user_info.getUserId() + "-sign.png";
        String filePath = directoryPath + fileName;

        // 创建 File 对象
        File file = new File(filePath);

        // 确保文件夹存在
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            ImageIO.write(bufferedImage, "png", file);

            // 构建签到成功返回消息
            String msg = Msg.builder().reply(event.getMessageId())
                    .at(user_info.getUserId())
                    .text(" 成功签到-")
                    .text(DateUtils.format(user_info.getSignInTime(),"yyyy/MM/dd") + "\t\n")
                    .text("签到次数 %d；连续签到 %d。".formatted(user_info.getSignInNum(),user_info.getSignInContNum()))
                    .build();
            // 发送签到成功信息
            bot.sendMsg(event, msg, false);
            msg = Msg.builder()
                    .img("file:///" + file.getAbsolutePath())
                    .build();
            // 发送签到图像
            bot.sendMsg(event, msg, false);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        if (groupTask == null){
            groupTask = new GroupTask(group_id);
        }

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
        HttpUtils.Body body = HttpUtils.sendGet("http://dwz.2xb.cn/zaob", "");

        String url = JSONObject.parseObject(body.getBody()).getString("imageUrl");

        String msg = Msg.builder()
                .img(url)
                .build();

        bot.sendMsg(event, msg, false);
    }

    /**
     * 今日运势事件
     * @param bot Bot
     * @param event Event
     */
    public static void Fortune(Bot bot, AnyMessageEvent event){
        Random random = new Random();
        String path = "/static/img/jrys/arknights/"+(random.nextInt(33)+1)+".jpg";
        // 绘制签到图像
        BufferedImage bufferedImage = GraphicUtils
                .graphicFortune(path);

        String directoryPath = "./data/img/Fortune/";
        String fileName = DateUtils.format(new Date(), "yyyy-MM-dd") +"-" + event.getUserId() + "fortune.png";
        String filePath = directoryPath + fileName;

        // 创建 File 对象
        File file = new File(filePath);

        // 确保文件夹存在
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        UserInfo userInfo = userInfoRepository.findByUserId(event.getUserId());

        Date date = new Date();

        if (userInfo == null) {

            // 初始化用户信息
            userInfo = creatUserInfo(event.getUserId());
            userInfo.setFortuneTime(date);

            String msg = Msg.builder()
                    .at(event.getUserId())
                    .text(" 今日运势")
                    .img("file:///" + file.getAbsolutePath())
                    .build();

            bot.sendMsg(event, msg, false);


        } else {

            if (DateUtils.isYesterdayOrEarlier(userInfo.getFortuneTime(),date)) {
                String msg = Msg.builder()
                        .at(event.getUserId())
                        .text(" 今日运势")
                        .img("file:///" + file.getAbsolutePath())
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
        }

        userInfo.setFortuneTime(date);
        userInfoRepository.save(userInfo);
    }

    /**
     * 获取签到的背景图片
     * @return 背景图片本地保存地址
     */
    private static String getBackground(){
        HttpUtils.Body body = HttpUtils.sendGetFile("https://iw233.cn/api.php"
                ,"sort=pc"
                , Headers.of("referer", "https://weibo.com/").newBuilder());


        String fileName = DateUtils.format(new Date(), "yyyy-MM-dd-HH_mm_ss") + "-bg.png";

        String filePath = FileUtils.saveImageFile(body.getFile(), "./data/img/Background/", fileName);

        // 判断大小是否合适
        if ( !GraphicUtils.isSuitable(filePath) ){
            return getBackground();
        }

        return filePath;
    }

    /**
     * 获取QQ头像
     * @param user_id 用户id
     * @return 用户头像
     */
    private static String getQqFace(long user_id){
        HttpUtils.Body body = HttpUtils.sendGetFile("https://qlogo2.store.qq.com/qzone/"+user_id+"/"+user_id+"/100"
                ,""
                , Headers.of("*", "*").newBuilder());

        String fileName = DateUtils.format(new Date(), "yyyy-MM-dd-HH_mm_ss") +"-"+ user_id + "-face.png";

        return FileUtils.saveImageFile(body.getFile(), "./data/img/QqFace/", fileName);
    }

    private static UserInfo creatUserInfo(long user_id){
        return new UserInfo(user_id,new Date(0),new Date(0),0,0,1);
    }


    /**
     * 抽取塔罗牌事件
     * @param bot Bot
     * @param event Event
     */
    public static void getTarot(Bot bot, AnyMessageEvent event){

        bot.sendMsg(event,"少女祷告中...",false);

        // 获取牌数据
        String[][] tarots = TarotMaster.getTarots(1);

        // 构建返回信息
        List<String> msgList = new ArrayList<>();
        if (tarots[0][1].equals("0")){
            msgList.add("【顺位】 的 【" + tarots[0][0] + "】");
        } else {
            msgList.add("【逆位】 的 【" + tarots[0][0] + "】");
        }
        msgList.add(tarots[0][2]);
        msgList.add("解牌：");
        msgList.add(tarots[0][3]);

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
        List<String> msgList = new ArrayList<>();
        for (int i=0 ; i<num ; i++){
            msgList.add("第 "+ (i+1) +" 张：");
            if (tarots[i][1].equals("0")){
                msgList.add("【顺位】 的 【" + tarots[i][0] + "】");
            } else {
                msgList.add("【逆位】 的 【" + tarots[i][0] + "】");
            }
            msgList.add(tarots[i][2]);
            msgList.add("解牌：");
            msgList.add(tarots[i][3]);
        }

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

    //TODO:错误情况返回信息
}
