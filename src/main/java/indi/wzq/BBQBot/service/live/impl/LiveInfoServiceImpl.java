package indi.wzq.BBQBot.service.live.impl;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.service.live.LiveInfoService;
import indi.wzq.BBQBot.utils.SpringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LiveInfoServiceImpl implements LiveInfoService {
    private static final LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);

    /**
     * 获取所有房间号
     * @return 房间号的列表
     */
    @Override
    public List<String> findAllRoomIds() {
        return liveInfoRepository.findAllRoomId();
    }

    /**
     * 保存直播间信息
     * @param liveInfo 直播间信息
     */
    @Override
    public void saveLiveInfo(LiveInfo liveInfo) {
        liveInfoRepository.save(liveInfo);
    }

    /**
     * 通过直播id获取直播间信息
     * @param room_id 直播id
     * @return 直播间信息
     */
    @Override
    public LiveInfo findLiveInfoByRoomID(String room_id) {
        return liveInfoRepository.findLiveByRoomId(room_id);
    }

    /**
     * 获取所有直播id和直播状态的键值对
     * @return 直播id-直播状态
     */
    @Override
    public Map<String, Integer> findAllRoomIdToStatus() {
        Map<String,Integer> result = new HashMap<>();
        List<String> allRoomId = liveInfoRepository.findAllRoomId();
        for (String roomId : allRoomId){
            result.put(roomId,liveInfoRepository.findStatusByRoomId(roomId));
        }
        return result;
    }

    /**
     * 通过房间id更改直播间状态
     * @param room_id 直播id
     * @param status_code 状态码
     */
    @Override
    public void updateLiveInfoByRoomId(String room_id , Integer status_code) {
        LiveInfo liveInfo = liveInfoRepository.findLiveByRoomId(room_id);
        liveInfo.setStatus(status_code);
        liveInfoRepository.save(liveInfo);
    }

}
