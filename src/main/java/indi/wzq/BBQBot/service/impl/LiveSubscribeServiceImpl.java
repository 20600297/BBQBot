package indi.wzq.BBQBot.service.impl;

import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import indi.wzq.BBQBot.repo.LiveSubscribeRepository;
import indi.wzq.BBQBot.service.LiveSubscribeService;
import indi.wzq.BBQBot.utils.SpringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveSubscribeServiceImpl implements LiveSubscribeService {

    private static final LiveSubscribeRepository liveSubscribeRepository = SpringUtils.getBean(LiveSubscribeRepository.class);

    /**
     * 保存订阅信息
     * @param liveSubscribe 订阅信息
     */
    @Override
    public void saveLiveSubscribe(LiveSubscribe liveSubscribe) {
        liveSubscribeRepository.save(liveSubscribe);
    }


    /**
     * 通过直播id获取订阅信息
     * @param room_id 直播id
     * @return 订阅信息列表
     */
    @Override
    public List<LiveSubscribe> findAllByRoomId(String room_id) {
        return liveSubscribeRepository.findAllByRoomId(room_id);
    }

    /**
     * 通过GroupId和RoomId判断是否存在订阅
     * @param group_id BotID
     * @param room_id 房间号
     * @return 是否存在的布尔值
     */
    @Override
    public boolean existsByGroupIdAndRoomId(long group_id, String room_id) {
        return liveSubscribeRepository.existsByGroupIdAndRoomId(group_id,room_id);
    }

}
