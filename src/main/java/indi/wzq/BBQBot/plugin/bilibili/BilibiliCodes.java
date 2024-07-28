package indi.wzq.BBQBot.plugin.bilibili;

import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.entity.bilibili.LiveSubscribe;
import indi.wzq.BBQBot.enums.Codes;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.repo.LiveSubscribeRepository;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.List;

public class BilibiliCodes {

    private static final LiveSubscribeRepository liveSubscribeRepository = SpringUtils.getBean(LiveSubscribeRepository.class);

    private static final BotContainer botContainer = SpringUtils.getBean(BotContainer.class);

    private static final LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);


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
            long bot_id = bot.getLoginInfo().getData().getUserId();
            long group_id = event.getGroupId();

            // 判断是否已经订阅
            if( liveSubscribeRepository.existsByGroupIdAndRoomId(group_id,room_id) ){
                bot.sendMsg(event, "订阅失败！\r\n%s 已经被订阅了呢。".formatted(room_id), false);
                return;
            }

            // 构建订阅信息
            LiveSubscribe liveSubscribe = new LiveSubscribe(
                    bot_id,
                    event.getGroupId(),
                    room_id
            );

            // 保存订阅信息
            liveSubscribeRepository.save(liveSubscribe);

            // 保存直播间信息
            liveInfoRepository.save(liveInfo);

            // 构建订阅成功返回消息
            String msg = Msg.builder().text("成功订阅-\r\n")
                    .img(liveInfo.getFace())
                    .text(liveInfo.getUname() + "\r\n的直播间！")
                    .build();

            // 发送订阅成功信息
            bot.sendMsg(event, msg, false);

        } else {

            // 发送订阅状态异常信息
            bot.sendMsg(event, "直播间状态异常，请检查房间号是否正确！", false);

        }
    }

    /**
     * 开播事件
     * @param room_id 直播id
     */
    public static void liveStart(String room_id,LiveInfo live_info){

        // 获取所有订阅此直播间的订阅信息
        List<LiveSubscribe> allSubscribe = liveSubscribeRepository.findAllByRoomId(room_id);

        // 更新 状态码 开播时间
        liveInfoRepository.save(live_info);

        // 构建开播提醒消息
        String msg = Msg.builder().text(live_info.getUname() + " 开播了！\r\n")
                .img(live_info.getCover())
                .text(live_info.getTitle())
                .build();

        // 遍历所有订阅此直播的订阅信息
        for (LiveSubscribe subscribe : allSubscribe){
            // 发送推送信息
            botContainer.robots.get(subscribe.getBotId())
                    .sendGroupMsg(subscribe.getGroupId(), msg, false);
        }
    }

    /**
     * 下播事件
     * @param room_id 直播id
     */
    public static void liveStop(String room_id,LiveInfo live_info){

        // 获取所有订阅此直播间的订阅信息
        List<LiveSubscribe> allSubscribe = liveSubscribeRepository.findAllByRoomId(room_id);

        long startDate = liveInfoRepository.findStartTimeByRoomId(room_id);
        long nowDate = (System.currentTimeMillis() / 1000);
        float hour = ( (nowDate - startDate) / 3600f );

        // 构建下播提醒消息
        String msg = Msg.builder().text(live_info.getUname() + " 下播了！\r\n")
                .img(live_info.getCover())
                .text("今天播了 %.2f 个小时呢！".formatted(hour))
                .build();

        liveInfoRepository.save(live_info);

        // 遍历所有订阅此直播的订阅信息
        for (LiveSubscribe subscribe : allSubscribe){
            // 发送推送信息
            botContainer.robots.get(subscribe.getBotId())
                    .sendGroupMsg(subscribe.getGroupId(), msg, false);
        }

    }
}