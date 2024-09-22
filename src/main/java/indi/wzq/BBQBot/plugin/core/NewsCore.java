package indi.wzq.BBQBot.plugin.core;

import com.alibaba.fastjson2.JSONObject;
import indi.wzq.BBQBot.entity.group.GroupInfo;
import indi.wzq.BBQBot.entity.group.GroupTask;
import indi.wzq.BBQBot.repo.GroupInfoRepository;
import indi.wzq.BBQBot.repo.GroupTaskRepository;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.http.HttpUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

public class NewsCore {

    /**
     * 订阅每日早报
     * @param group_id 群号
     * @param bot_id bot号
     * @return 返回信息
     */
    public static String subscribe(long group_id , long bot_id){
        GroupInfoRepository groupInfoRepository = SpringUtils.getBean(GroupInfoRepository.class);
        GroupTaskRepository groupTaskRepository = SpringUtils.getBean(GroupTaskRepository.class);

        GroupTask groupTask = groupTaskRepository.findByGroupId(group_id);

        // 校验是否存在
        if (groupTask == null)
            groupTask = new GroupTask(group_id);

        // 判断是否已经订阅
        else if( groupTask.getDailyNews() ) {
            return "订阅失败！\r\n已经订阅过每日早报了！";
        }

        // 更改订阅状态并保存
        groupTask.setDailyNews(true);
        groupTaskRepository.save(groupTask);

        // 校验群信息是否存在
        if (groupInfoRepository.findGroupInfoByGroupIdAndBotId(group_id, bot_id) == null) {
            GroupInfo groupInfo =new GroupInfo(group_id,bot_id);
            groupInfoRepository.save(groupInfo);
        }

        return Msg.builder()
                .text("每日早报-订阅成功")
                .build();
    }

    /**
     * @return 今日早报返回文本
     */
    public static String todayNews(){

        HttpUtils.Body body = HttpUtils.sendGet("http://dwz.2xb.cn/zaob");

        String url = JSONObject.parseObject(body.getBody()).getString("imageUrl");

        return Msg.builder()
                .img(url)
                .build();

    }

    /**
     * @return 帮助文本
     */
    public static String getHelp(){
        return Msg.builder()
                .text("--- 早报相关 ---\r\n")
                .text("指令：“订阅每日早报”\r\n")
                .text("功能：每日早八推送早报信息\r\n")
                .text("\r\n")
                .text("指令：“今日早报”\r\n")
                .text("功能：获取当天的早报\r\n")
                .text("--------------")
                .build();
    }

}
