package indi.wzq.BBQBot.plugin.bilibili;

import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import indi.wzq.BBQBot.enums.Codes;
import indi.wzq.BBQBot.service.LiveInfoService;
import indi.wzq.BBQBot.service.LiveSubscribeService;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;

public class BilibiliCodes {

    private static final LiveInfoService liveInfoService = SpringUtils.getBean(LiveInfoService.class);

    private static final LiveSubscribeService liveSubscribeService = SpringUtils.getBean(LiveSubscribeService.class);

    /***
     * 订阅事件
     * @param bot Bot
     * @param event Event
     */
    public static void subscribe(Bot bot, AnyMessageEvent event) {

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        // 获取目标房间id
        String room_id = event.getRawMessage().replaceAll(Codes.LIVE_SUBSCRIBE.getStr(), "").trim();

        // 判断是否为空
        if (room_id.isEmpty()) {
            bot.sendMsg(event, "订阅直播间指令后面加上要订阅的房间号！", false);
        }

        // 通过房间id获取直播间信息
        LiveInfo liveInfo = BilibiliUtils.getLiveInfoByRoomId(room_id);

        // 判断信息是否正常获取
        if (liveInfo != null){

            // 构建订阅信息
            LiveSubscribe liveSubscribe = new LiveSubscribe(
                    bot.getLoginInfo().getData().getUserId(),
                    event.getGroupId(),
                    room_id
            );

            // 保存订阅信息
            liveSubscribeService.saveLiveSubscribe(liveSubscribe);

            // 保存直播间信息
            liveInfoService.saveLiveInfo(liveInfo);

            // 发送订阅成功信息
            bot.sendMsg(event, "订阅成功！%s \r\n当前直播状态为 %d。".formatted(room_id, liveInfo.getStatus()), false);

        } else {

            // 发送订阅状态异常信息
            bot.sendMsg(event, "直播间状态异常，请检查房间号是否正确！", false);

        }
    }
}