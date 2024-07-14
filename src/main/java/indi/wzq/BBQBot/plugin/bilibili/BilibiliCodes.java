package indi.wzq.BBQBot.plugin.bilibili;

import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import indi.wzq.BBQBot.enums.Codes;
import indi.wzq.BBQBot.service.LiveInfoService;
import indi.wzq.BBQBot.service.LiveSubscribeService;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.List;

public class BilibiliCodes {

    private static final BotContainer botContainer = SpringUtils.getBean(BotContainer.class);

    private static final LiveInfoService liveInfoService = SpringUtils.getBean(LiveInfoService.class);

    private static final LiveSubscribeService liveSubscribeService = SpringUtils.getBean(LiveSubscribeService.class);

    /**
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
            return;
        }

        // 通过房间id获取直播间信息
        LiveInfo liveInfo = BilibiliUtils.getLiveInfoByRoomId(room_id);

        // 判断信息是否正常获取
        if (liveInfo != null){

            // 获取BotId
            long botId = bot.getLoginInfo().getData().getUserId();

            // 判断是否已经订阅
            if( liveSubscribeService.existsByBotIdAndRoomId(botId,room_id) ){
                bot.sendMsg(event, "订阅失败！\r\n%s 已经被订阅了呢。".formatted(room_id), false);
                return;
            }

            // 构建订阅信息
            LiveSubscribe liveSubscribe = new LiveSubscribe(
                    botId,
                    event.getGroupId(),
                    room_id
            );

            // 保存订阅信息
            liveSubscribeService.saveLiveSubscribe(liveSubscribe);

            // 保存直播间信息
            liveInfoService.saveLiveInfo(liveInfo);

            // 构建订阅成功返回消息
            String msg = Msg.builder().text("成功订阅-\r\n")
                    .img(liveInfo.getFace())
                    .text(liveInfo.getUname() + "\r\n的直播间！")
                    .build();

            // 发送订阅成功信息
            bot.sendMsg(event, msg, true);

        } else {

            // 发送订阅状态异常信息
            bot.sendMsg(event, "直播间状态异常，请检查房间号是否正确！", false);

        }
    }

    /**
     * 开播事件
     * @param room_id 直播id
     */
    public static void liveStart(String room_id){
        // 更新状态码
        liveInfoService.updateLiveInfoByRoomId(room_id,1);

        // 获取直播间信息
        LiveInfo liveInfo = BilibiliUtils.getLiveInfoByRoomId(room_id);

        // 获取所有订阅此直播间的订阅信息
        List<LiveSubscribe> allSubscribe = liveSubscribeService.findAllByRoomId(room_id);

        // 判断是否正常获取直播间信息
        if(liveInfo == null){
            liveInfo = liveInfoService.findLiveInfoByRoomID(room_id);
        }

        // 构建开播提醒消息
        String msg = Msg.builder().text(liveInfo.getUname() + " 开播了！\r\n")
                .img(liveInfo.getCover())
                .text(liveInfo.getTitle())
                .build();

        // 遍历所有订阅此直播的订阅信息
        for (LiveSubscribe subscribe : allSubscribe){
            // 发送推送信息
            botContainer.robots.get(subscribe.getBotId())
                    .sendGroupMsg(subscribe.getGroupId(), msg, true);
        }
    }
}