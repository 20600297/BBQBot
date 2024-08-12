package indi.wzq.BBQBot.plugin.bilibili;

import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.entity.bilibili.Dynamic.Dynamic;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.entity.bilibili.UpInfo;
import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import indi.wzq.BBQBot.entity.group.UpSubscribe;
import indi.wzq.BBQBot.enums.Codes;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.repo.LiveSubscribeRepository;
import indi.wzq.BBQBot.repo.UpInfoRepository;
import indi.wzq.BBQBot.repo.UpSubscribeRepository;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.List;

public class BilibiliCodes {

    /**
     * 订阅直播事件
     * @param bot Bot
     * @param event Event
     */
    public static void LiveSubscribe(Bot bot, AnyMessageEvent event) {
        LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);
        LiveSubscribeRepository liveSubscribeRepository = SpringUtils.getBean(LiveSubscribeRepository.class);

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        // 获取目标房间id
        String room_id = event.getRawMessage().replaceAll(Codes.LIVE_SUBSCRIBE.getStr(), "").trim();
        if (room_id.isEmpty()) {
            bot.sendMsg(event, "订阅直播间指令后面加上要订阅的房间号！", false);
            return;
        }

        // 判断是否已经订阅
        if( liveSubscribeRepository.existsByGroupIdAndRoomId(event.getGroupId(), room_id) ){
            bot.sendMsg(event, "订阅失败！\r\n%s 已经被订阅了呢。".formatted(room_id), false);
            return;
        }

        // 通过房间id获取直播间信息
        LiveInfo liveInfo = BilibiliUtils.getLiveInfoByRoomId(room_id);
        if (liveInfo == null) {
            String msg = Msg.builder()
                    .text("订阅失败！\r\n")
                    .text("[" + room_id + "]" + "信息获取失败\r\n")
                    .build();
            bot.sendMsg(event, msg, false);
            return;
        }

        // 保存直播间信息
        liveInfoRepository.save(liveInfo);


        // 构建订阅信息并储存
        LiveSubscribe liveSubscribe = new LiveSubscribe(
                bot.getSelfId(),
                event.getGroupId(),
                room_id
        );
        liveSubscribeRepository.save(liveSubscribe);

        // 构建订阅成功返回消息
        String msg = Msg.builder().text("成功订阅-\r\n")
                .img(liveInfo.getFace())
                .text(liveInfo.getUname() + "\r\n的直播间！")
                .build();

        // 发送订阅成功信息
        bot.sendMsg(event, msg, false);
    }

    /**
     * 订阅UP事件
     * @param bot Bot
     * @param event Event
     */
    public static void UpSubscribe(Bot bot, AnyMessageEvent event){
        UpSubscribeRepository upSubscribeRepository = SpringUtils.getBean(UpSubscribeRepository.class);

        // 判断是否为群组触发
        if (!ActionParams.GROUP.equals(event.getMessageType())) {
            bot.sendMsg(event, "此指令只能在群组中使用！", false);
            return;
        }

        // 获取目标up的uid
        String mid = event.getRawMessage().replaceAll(Codes.UP_SUBSCRIBE.getStr(), "").trim();
        // 判断是否为空
        if (mid.isEmpty()) {
            bot.sendMsg(event, "订阅UP指令后面加上要订阅的UID！", false);
            return;
        }

        // 判断是否已经订阅
        if (upSubscribeRepository.existsByGroupIdAndMid(event.getGroupId(), mid)) {
            String msg = Msg.builder()
                    .text("订阅失败！\r\n")
                    .text("[" + mid + "]" + "已经订阅了呢\r\n")
                    .build();
            bot.sendMsg(event, msg, false);
            return;
        }

        // 获取 Up信息
        UpInfo upInfo = BilibiliUtils.getUpInfoByUID(mid);
        if (upInfo == null) {
            String msg = Msg.builder()
                    .text("订阅失败！\r\n")
                    .text("[" + mid + "]" + "信息获取失败\r\n")
                    .build();
            bot.sendMsg(event, msg, false);
            return;
        }

        // 构建订阅信息并储存
        UpSubscribe upSubscribe = new UpSubscribe(
                bot.getSelfId(),
                event.getGroupId(),
                mid);
        upSubscribeRepository.save(upSubscribe);

        String msg = Msg.builder()
                .text("成功订阅-\n")
                .img(upInfo.getFace())
                .text("[" + upInfo.getUname() + "]")
                .build();
        bot.sendMsg(event, msg, false);

        Dynamic newDynamic = BilibiliUtils.getUpNewDynamic(mid);
        upInfo.setDynamic(newDynamic);

        // 储存UP信息
        SpringUtils.getBean(UpInfoRepository.class).save(upInfo);
    }

    /**
     * 开播事件
     * @param room_id 直播id
     */
    public static void liveStart(String room_id,LiveInfo live_info){
        LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);
        BotContainer botContainer = SpringUtils.getBean(BotContainer.class);
        LiveSubscribeRepository liveSubscribeRepository = SpringUtils.getBean(LiveSubscribeRepository.class);

        // 获取所有订阅此直播间的订阅信息
        List<LiveSubscribe> allSubscribe = liveSubscribeRepository.findAllByRoomId(room_id);

        // 更新 状态码 开播时间
        liveInfoRepository.save(live_info);

        // 构建 返回消息
        String msg = Msg.builder()
                .atAll()
                .text(live_info.getUname() + " 开播了！\r\n")
                .img(live_info.getCover())
                .text(live_info.getTitle())
                .build();

        // 遍历所有订阅此直播的订阅信息
        for (LiveSubscribe subscribe : allSubscribe){
            Long groupId = subscribe.getGroupId();

            // 发送推送信息
            Bot bot = botContainer.robots.get(subscribe.getBotId());

            bot.sendGroupMsg(groupId, msg, false);
        }
    }

    /**
     * 下播事件
     * @param room_id 直播id
     */
    public static void liveStop(String room_id,LiveInfo live_info){
        LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);
        BotContainer botContainer = SpringUtils.getBean(BotContainer.class);
        LiveSubscribeRepository liveSubscribeRepository = SpringUtils.getBean(LiveSubscribeRepository.class);

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

    /**
     * 新动态事件
     * @param newDynamic 新动态信息
     */
    public static void newDynamic(UpInfo upInfo ,Dynamic newDynamic){
        UpSubscribeRepository upSubscribeRepository = SpringUtils.getBean(UpSubscribeRepository.class);
        BotContainer botContainer = SpringUtils.getBean(BotContainer.class);

        List<UpSubscribe> allSubscribe = upSubscribeRepository.findAllByMid(upInfo.getMid());

        String msg;
        switch (newDynamic.getType()){
            case "DYNAMIC_TYPE_AV" ->{
                // 发布视频
                msg = Msg.builder()
                        .atAll()
                        .text("订阅的UP [" + upInfo.getUname() + "]\r\n")
                        .text("发布新视频啦！快去围观！\r\n")
                        .text(newDynamic.getJumpUrl())
                        .build();
            }
            case "DYNAMIC_TYPE_DRAW" -> {
                // 图文动态
                msg = Msg.builder()
                        .atAll()
                        .text("订阅的UP [" + upInfo.getUname() + "]\r\n")
                        .text("发布新的图文动态啦！快去围观！\r\n")
                        .text(newDynamic.getJumpUrl())
                        .build();
            }
            default -> {
                msg = Msg.builder()
                        .atAll()
                        .text("订阅的UP [" + upInfo.getUname() + "]\r\n")
                        .text("发布新的动态啦！快去围观！\r\n")
                        .text(newDynamic.getJumpUrl())
                        .build();
            }
        }

        for (UpSubscribe subscribe : allSubscribe){
            // 发送推送信息
            botContainer.robots.get(subscribe.getBotId())
                    .sendGroupMsg(subscribe.getGroupId(), msg, false);
        }

    }
}