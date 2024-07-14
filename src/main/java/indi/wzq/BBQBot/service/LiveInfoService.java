package indi.wzq.BBQBot.service;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;

import java.util.List;
import java.util.Map;

public interface LiveInfoService {

    /**
     * 获取所有房间号
     * @return 房间号的列表
     */
    List<String> findAllRoomIds();

    /**
     * 保存直播间信息
     * @param liveInfo 直播间信息
     */
    void saveLiveInfo(LiveInfo liveInfo);

    /**
     * 通过直播id获取直播间信息
     * @param room_id 直播id
     * @return 直播间信息
     */
    LiveInfo findLiveInfoByRoomID(String room_id);

    /**
     * 获取所有直播id和直播状态的键值对
     * @return 直播id-直播状态
     */
    Map<String,Integer> findAllRoomIdToStatus();

    /**
     * 通过房间id获取直播间状态
     * @param room_id 房间id
     * @return 状态码
     */
    Integer findStatusByRoomId(String room_id);

    /**
     * 通过房间id更新状态码
     * @param room_id 房间id
     * @param status_code 状态码
     */
    void updateLiveInfoByRoomId(String room_id , Integer status_code);

}
