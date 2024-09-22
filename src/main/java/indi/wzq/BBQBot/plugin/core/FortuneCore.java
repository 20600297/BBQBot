package indi.wzq.BBQBot.plugin.core;

import indi.wzq.BBQBot.entity.group.UserInfo;
import indi.wzq.BBQBot.repo.UserInfoRepository;
import indi.wzq.BBQBot.utils.DateUtils;
import indi.wzq.BBQBot.utils.FileUtils;
import indi.wzq.BBQBot.utils.Graphic.GraphicUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Random;

public class FortuneCore {

    /**
     * 获取今日运势
     * @param user_id QQ号
     * @return 返回信息
     */
    public static String getFortune(long user_id){
        UserInfoRepository userInfoRepository = SpringUtils.getBean(UserInfoRepository.class);

        Random random = new Random();
        String path = "/static/img/jrys/"+(random.nextInt(33)+1)+".jpg";
        // 绘制签到图像
        BufferedImage bufferedImage = GraphicUtils
                .graphicFortune(path);

        Date date = new Date();

        UserInfo userInfo = userInfoRepository.findByUserId(user_id);

        // 如果未获得到用户信息则初始化用户信息
        if (userInfo == null)
            userInfo = new UserInfo(user_id);

        if (DateUtils.isYesterdayOrEarlier(userInfo.getFortuneTime(),date)) {

            userInfo.setFortuneTime(date);
            userInfoRepository.save(userInfo);

            return Msg.builder()
                    .at(user_id)
                    .text(" 今日运势")
                    .imgBase64(FileUtils.bufferedImage2Bytes(bufferedImage))
                    .build();

        } else {
            return Msg.builder()
                    .at(user_id)
                    .text(" 每人一天限抽签1次呢！\r\n")
                    .text("贪心的人是不会有好运的。")
                    .build();

        }

    }

    /**
     * @return 帮助文本
     */
    public static String getHelp(){

        return Msg.builder()
                .text("--- 今日运势 ---\r\n")
                .text("指令：“今日运势”\r\n")
                .text("功能：抽取今日运势\r\n")
                .text("--------------")
                .build();

    }

}
