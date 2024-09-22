package indi.wzq.BBQBot.task;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.plugin.code.BilibiliCodes;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TaskBilibiliLiveInfo {
    private static final LiveInfoRepository liveInfoRepository = SpringUtils.getBean(LiveInfoRepository.class);

    /**
     * 直播间状态查询定时任务
     */
    @Async("taskExecutor")
    @Scheduled(cron = "0 * * * * ?")
    public void liveStatus() {

        // 获取所有 房间id
        List<String> roomIds = liveInfoRepository.findAllRoomId();

        // 获取数据库中所有 直播id-直播状态 的键值对
        Map<String,Integer> roomIdToStatus = findAllRoomIdToStatus();

        // 遍历订阅列表
        for(String roomId : roomIds){

            // 通过房间id获取直播间状态
            LiveInfo liveInfo =  BilibiliUtils.getLiveInfoByRoomId(roomId);

            if (liveInfo == null) {
                continue;
            }

            Integer statusCode = liveInfo.getStatus();

            // 判断状态码是否改变
            if (!statusCode.equals(roomIdToStatus.get(roomId))){

                System.out.println("直播间 " + roomId + " 状态变化为 " + statusCode);

                //TODO 完善直播间状态变化提示
                switch (statusCode) {
                    // 下播事件
                    case  0 -> BilibiliCodes.liveStop(roomId,liveInfo);
                    // 开播事件
                    case  1 -> BilibiliCodes.liveStart(roomId,liveInfo);
                    // 轮播事件
                    case  2 -> updateLiveInfoByRoomId(roomId,2);
                    // 直播间异常事件
                    case -2 -> updateLiveInfoByRoomId(roomId,-2);
                    // 未知情况
                    default -> {
                        System.out.println("直播间 " + roomId + " 状态异常，状态码- " + statusCode);
                        updateLiveInfoByRoomId(roomId,-2);
                    }
                }
            }
        }
    }

    /**
     * 获取所有直播id和直播状态的键值对
     * @return 直播id-直播状态
     */
    private Map<String, Integer> findAllRoomIdToStatus() {
        Map<String,Integer> result = new HashMap<>();
        List<String> allRoomId = liveInfoRepository.findAllRoomId();
        for (String roomId : allRoomId){
            result.put(roomId,liveInfoRepository.findStatusByRoomId(roomId));
        }
        return result;
    }

    /**
     * 通过房间id更新状态码
     * @param room_id 房间id
     * @param status_code 状态码
     */
    private void updateLiveInfoByRoomId(String room_id , Integer status_code) {
        LiveInfo liveInfo = liveInfoRepository.findLiveByRoomId(room_id);
        liveInfo.setStatus(status_code);
        liveInfoRepository.save(liveInfo);
    }
}
