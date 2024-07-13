package indi.wzq.BBQBot.service;

import indi.wzq.BBQBot.entity.group.LiveSubscribe;

import java.util.List;

public interface LiveSubscribeService {

    /***
     * 保存订阅信息
     * @param liveSubscribe 订阅信息
     */
    void saveLiveSubscribe(LiveSubscribe liveSubscribe);

    /***
     * 获取所有订阅信息
     * @return 订阅信息列表
     */
    List<LiveSubscribe> findAllLiveSubscribe();

}
