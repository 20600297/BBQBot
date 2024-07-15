package indi.wzq.BBQBot.plugin.group;

import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.group.UserInfo;
import indi.wzq.BBQBot.service.UserInfoService;
import indi.wzq.BBQBot.utils.DateUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.Date;


public class GroupCodes {

    private static final UserInfoService userInfoService = SpringUtils.getBean(UserInfoService.class);

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
        UserInfo userInfo = userInfoService.findUserInfoByUserId(signUserId);

        // 获取当前时间
        Date signInTime = new Date();

        // 判断是否为初次签到
        if (userInfo == null) {

            // 初始化用户信息
            userInfo = new UserInfo(signUserId,signInTime,1,1,1);
            userInfoService.saveUserInfo(userInfo);

            // 构建签到成功返回消息
            String msg = Msg.builder().reply(event.getMessageId())
                    .at(signUserId)
                    .text("成功签到-")
                    .text(DateUtils.format(signInTime,"yyyy/MM/dd") + "\t\n")
                    .text("签到次数 1；连续签到 1。")
                    .build();

            // 发送签到成功信息
            bot.sendMsg(event, msg, true);

        } else {

            // 判断今日是否签到
            if (DateUtils.isSameDay(signInTime,userInfo.getSignInTime())){

                // 构建消息
                String msg = Msg.builder().reply(event.getMessageId())
                        .at(signUserId)
                        .text("您今天已经签过到了呢！")
                        .build();

                // 发送信息
                bot.sendMsg(event, msg, true);

            } else {

                // 判断是否为连续签到
                if (DateUtils.isYesterday(userInfo.getSignInTime(),signInTime)){
                    userInfo.setSignInContNum(userInfo.getSignInContNum() + 1);
                } else {
                    userInfo.setSignInContNum(1);
                }

                userInfo.setSignInTime(signInTime);

                userInfo.setSignInNum(userInfo.getSignInNum() + 1);

                // 更新用户信息
                userInfoService.saveUserInfo(userInfo);

                // 构建签到成功返回消息
                String msg = Msg.builder().reply(event.getMessageId())
                        .at(signUserId)
                        .text("成功签到-")
                        .text(DateUtils.format(signInTime,"yyyy/MM/dd") + "\t\n")
                        .text("签到次数 %d；连续签到 %d。".formatted(userInfo.getSignInNum(),userInfo.getSignInContNum()))
                        .build();

                // 发送签到成功信息
                bot.sendMsg(event, msg, true);
            }
        }


    }

}
