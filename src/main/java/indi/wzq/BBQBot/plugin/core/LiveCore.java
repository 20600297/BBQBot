package indi.wzq.BBQBot.plugin.core;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.repo.LiveSubscribeRepository;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

public class LiveCore {

    /**
     * 直播间订阅
     * @param room_id 房间号
     * @param bot_id bot号
     * @param group_id 群号
     * @return 返回信息
     */
    public static String subscribe(String room_id , long bot_id , long group_id){

        // 通过房间号获取直播间信息
        LiveInfo liveInfo = BilibiliUtils.getLiveInfoByRoomId(room_id);
        if (liveInfo == null) {
            return Msg.builder()
                    .text("订阅失败！\r\n")
                    .text("[" + room_id + "]\r\n")
                    .text("信息获取失败")
                    .build();
        }

        // 保存直播间信息
        SpringUtils.getBean(LiveInfoRepository.class).save(liveInfo);

        // 构建订阅信息并储存
        LiveSubscribe liveSubscribe = new LiveSubscribe(
                bot_id,
                group_id,
                room_id
        );
        SpringUtils.getBean(LiveSubscribeRepository.class).save(liveSubscribe);

        // 构建订阅成功返回消息
        return Msg.builder()
                .text("成功订阅-\r\n")
                .img(liveInfo.getFace())
                .text(liveInfo.getUname() + "\r\n的直播间！")
                .build();

    }

    public static void unSubscribe(){

    }

    /**
     * @return 帮助文本
     */
    public static String getHelp(){
        return Msg.builder()
                .text("--- 直播间相关 ---\r\n")
                .text("指令：”订阅{1}“\r\n")
                .text("参数：1——房间号\r\n")
                .text("  房间号：直播间的room_id\r\n")
                .text("功能：订阅直播间并定时推送相关信息\r\n")
                .text("----------------")
                .build();
    }

}
