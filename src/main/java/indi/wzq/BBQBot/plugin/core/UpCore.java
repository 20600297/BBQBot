package indi.wzq.BBQBot.plugin.core;

import indi.wzq.BBQBot.entity.bilibili.UpInfo;
import indi.wzq.BBQBot.entity.group.UpSubscribe;
import indi.wzq.BBQBot.repo.UpInfoRepository;
import indi.wzq.BBQBot.repo.UpSubscribeRepository;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

public class UpCore {

    /**
     * Up订阅
     * @param up_uid Up主的uid
     * @param bot_id bot号
     * @param group_id 群号
     * @return 返回信息
     */
    public static String subscribe(String up_uid , long bot_id , long group_id){

        // 获取 Up信息
        UpInfo upInfo = BilibiliUtils.getUpInfoByUID(up_uid);
        if (upInfo == null) {
            return Msg.builder()
                    .text("订阅失败！\r\n")
                    .text("[" + up_uid + "]\r\n")
                    .text("信息获取失败")
                    .build();
        }

        // 保存Up信息
        SpringUtils.getBean(UpInfoRepository.class).save(upInfo);

        // 构建订阅信息并储存
        UpSubscribe upSubscribe = new UpSubscribe(
                bot_id,
                group_id,
                up_uid
        );
        SpringUtils.getBean(UpSubscribeRepository.class).save(upSubscribe);

        // 构建订阅成功返回消息
        return Msg.builder()
                .text("成功订阅-\r\n")
                .img(upInfo.getFace())
                .text("[" + upInfo.getUname() + "]")
                .build();

    }

    public static void unSubscribe(){

    }

    /**
     * @return 帮助文本
     */
    public static String getHelp(){
        return Msg.builder()
                .text("--- UP主相关 ---\r\n")
                .text("指令：”订阅{1}“\r\n")
                .text("参数：1——UID\r\n")
                .text("  房间号：UP主的UID\r\n")
                .text("功能：订阅UP主并定时推送相关信息\r\n")
                .text("---------------")
                .build();
    }
}
