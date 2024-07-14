package indi.wzq.BBQBot.service;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;

public interface LiveInfoService {

    /***
     * 保存直播间信息
     * @param liveInfo 直播间信息
     */
    void saveLiveInfo(LiveInfo liveInfo);

    /***
     * 通过房间id获取直播间状态
     * @param room_id 房间id
     * @return 状态码
     */
    Integer findStatusByRoomId(String room_id);

    /***
     * 通过房间id更新状态码
     * @param room_id 房间id
     * @param status_code 状态码
     */
    void updateLiveInfoByRoomId(String room_id , Integer status_code);

}
