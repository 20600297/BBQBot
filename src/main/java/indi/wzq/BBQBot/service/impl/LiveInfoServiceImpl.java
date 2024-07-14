package indi.wzq.BBQBot.service.impl;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.service.LiveInfoService;
import indi.wzq.BBQBot.utils.SpringUtils;
import org.springframework.stereotype.Service;


@Service
public class LiveInfoServiceImpl implements LiveInfoService {
    private final LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);

    /***
     * 保存直播间信息
     * @param liveInfo 直播间信息
     */
    @Override
    public void saveLiveInfo(LiveInfo liveInfo) {
        liveInfoRepository.save(liveInfo);
    }

    /***
     * 通过房间id获取直播间状态
     * @param room_id 房间id
     * @return 状态码
     */
    @Override
    public Integer findStatusByRoomId(String room_id) {
        return liveInfoRepository.findLiveByRoomId(room_id).getStatus();
    }

    /***
     * 通过房间id更改直播间状态
     * @param room_id 房间id
     * @param status_code 状态码
     */
    @Override
    public void updateLiveInfoByRoomId(String room_id , Integer status_code) {
        LiveInfo liveInfo = liveInfoRepository.findLiveByRoomId(room_id);
        liveInfo.setStatus(status_code);
        liveInfoRepository.save(liveInfo);
    }

}
