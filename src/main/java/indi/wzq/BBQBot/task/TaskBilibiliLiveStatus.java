package indi.wzq.BBQBot.task;

import indi.wzq.BBQBot.plugin.bilibili.BilibiliCodes;
import indi.wzq.BBQBot.service.LiveInfoService;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TaskBilibiliLiveStatus {

    // 信息控制器
    private static final LiveInfoService liveInfoService = SpringUtils.getBean(LiveInfoService.class);


    /**
     * 直播间状态查询定时任务
     */
    @Async("taskExecutor")
    @Scheduled(cron = "0 * * * * ?")
    public void execute() {

        // 获取所有 房间id
        List<String> roomIds = liveInfoService.findAllRoomIds();

        // 获取数据库中所有 直播id-直播状态 的键值对
        Map<String,Integer> roomIdToStatus = liveInfoService.findAllRoomIdToStatus();

        // 遍历订阅列表
        for(String roomId : roomIds){

            // 通过房间id获取直播间状态
            Integer statusCode =  BilibiliUtils.getLiveStatusByRoomId(roomId);

            // 判断状态码是否改变
            if (!statusCode.equals(roomIdToStatus.get(roomId))){

                System.out.println("直播间 " + roomId + " 状态变化为 " + statusCode);

                //TODO 完善直播间状态变化提示
                switch (statusCode) {
                    // 下播事件
                    case  0 -> BilibiliCodes.liveStop(roomId);
                    // 开播事件
                    case  1 -> BilibiliCodes.liveStart(roomId);
                    // 轮播事件
                    case  2 -> liveInfoService.updateLiveInfoByRoomId(roomId,2);
                    // 直播间异常事件
                    case -2 -> liveInfoService.updateLiveInfoByRoomId(roomId,-2);
                    // 未知情况
                    default -> {
                        System.out.println("直播间 " + roomId + " 状态异常，状态码- " + statusCode);
                        liveInfoService.updateLiveInfoByRoomId(roomId,-2);
                    }
                }
            }
        }
    }

    /**
     * 存活日志
     */
    @Async("taskExecutor")
    @Scheduled(cron = "0 0 * * * ?")
    public void life(){
        log.info("-----当前程序正常存活！-----");
    }
}
