package indi.wzq.BBQBot.service.live;

import indi.wzq.BBQBot.entity.bilibili.LiveSubscribe;

import java.util.List;

public interface LiveSubscribeService {

    /**
     * 保存订阅信息
     * @param liveSubscribe 订阅信息
     */
    void saveLiveSubscribe(LiveSubscribe liveSubscribe);

    /**
     * 通过直播id获取订阅信息
     * @param room_id 直播id
     * @return 订阅信息列表
     */
    List<LiveSubscribe> findAllByRoomId(String room_id);

    /**
     * 通过BotId和RoomId判断是否存在订阅
     * @param group_id BotID
     * @param room_id 直播id
     * @return 是否存在的布尔值
     */
    boolean existsByGroupIdAndRoomId(long group_id, String room_id);

}
