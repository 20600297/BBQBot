package indi.wzq.BBQBot.task;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.BotContainer;
import indi.wzq.BBQBot.service.group.GroupInfoService;
import indi.wzq.BBQBot.service.group.GroupTaskService;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.http.HttpUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TaskGrout {

    private static final GroupTaskService groupTaskService = SpringUtils.getBean(GroupTaskService.class);

    private static final GroupInfoService groupInfoService = SpringUtils.getBean(GroupInfoService.class);

    private static final BotContainer botContainer = SpringUtils.getBean(BotContainer.class);

    @Async("taskExecutor")
    @Scheduled(cron = "0 0 8 * * ?")
    public void dailyNews() {

        List<Long> groupIds = groupTaskService.findGroupIdByDailyNews(true);

        HttpUtils.Body body = HttpUtils.sendGet("http://dwz.2xb.cn/zaob", "");

        String url = JSONObject.parseObject(body.getBody()).getString("imageUrl");

        String msg = Msg.builder()
                .img(url)
                .build();

        for(long groupId : groupIds){
            botContainer.robots.get(groupInfoService.findBotIdByGroupId(groupId))
                    .sendGroupMsg(groupId,msg,true);
        }
    }
}
