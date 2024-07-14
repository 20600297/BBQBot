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

    /***
     * 保存订阅信息
     * @param liveSubscribe 订阅信息
     */
    @Override
    public void saveLiveSubscribe(LiveSubscribe liveSubscribe) {
        liveSubscribeRepository.save(liveSubscribe);
    }

    /***
     * 获取所有订阅信息
     * @return 订阅信息列表
     */
    @Override
    public List<LiveSubscribe> findAllLiveSubscribe() {
        return liveSubscribeRepository.findAll();
    }

    /***
     * 通过BotId和RoomId判断是否存在订阅
     * @param bot_id BotID
     * @param room_id 房间号
     * @return 是否存在的布尔值
     */
    @Override
    public boolean existsByBotIdAndRoomId(long bot_id, String room_id) {
        return liveSubscribeRepository.existsByBotIdAndRoomId(bot_id,room_id);
    }

}
