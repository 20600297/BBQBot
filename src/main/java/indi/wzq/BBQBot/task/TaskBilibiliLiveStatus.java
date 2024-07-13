package indi.wzq.BBQBot.task;


import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import indi.wzq.BBQBot.service.LiveInfoService;
import indi.wzq.BBQBot.service.LiveSubscribeService;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TaskBilibiliLiveStatus {

    // Bot工厂
    @Resource
    private BotContainer botContainer;

    // 信息控制器
    private static final LiveInfoService liveInfoService = SpringUtils.getBean(LiveInfoService.class);

    // 订阅控制器
    private static final LiveSubscribeService liveSubscribeService = SpringUtils.getBean(LiveSubscribeService.class);


    /***
     * 直播间状态查询定时任务
     */
    @Async("taskExecutor")
    @Scheduled(cron = "0 * * * * ?")
    public void execute() {

        // 获取所有订阅信息
        List<LiveSubscribe> subscribes = liveSubscribeService.findAllLiveSubscribe();

        // 遍历订阅列表
        for(LiveSubscribe subscribe : subscribes){

            // 从订阅信息中获取房间id
            String room_id =  subscribe.getRoomId();

            // 通过房间id获取直播间状态
            Integer statusCode =  BilibiliUtils.getLiveStatusByRoomId(room_id);

            // 判断状态码是否改变
            if (!statusCode.equals(liveInfoService.findStatusByRoomId(room_id))){

                // 输出日志
                log.info("直播间 " + room_id + " 状态变化为 " + statusCode);

                // 更新状态码
                liveInfoService.updateLiveByRoomId(room_id,statusCode);

                // 判断是否是更改为开播
                if(statusCode == 1){

                    //TODO:开播提示添加主播信息

                    // 获取订阅的Bot
                    Bot bot = botContainer.robots.get(subscribe.getBotId());

                    // 发送推送信息
                    bot.sendGroupMsg(subscribe.getGroupId(), "直播间 " + room_id + " 开播啦，快去围观！", false);

                }

            }
        }
    }
}
