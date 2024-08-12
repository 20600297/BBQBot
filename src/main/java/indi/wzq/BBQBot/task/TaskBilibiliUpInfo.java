package indi.wzq.BBQBot.task;

import indi.wzq.BBQBot.entity.bilibili.Dynamic.Dynamic;
import indi.wzq.BBQBot.entity.bilibili.UpInfo;
import indi.wzq.BBQBot.plugin.bilibili.BilibiliCodes;
import indi.wzq.BBQBot.repo.LiveInfoRepository;
import indi.wzq.BBQBot.repo.UpInfoRepository;
import indi.wzq.BBQBot.utils.BilibiliUtils;
import indi.wzq.BBQBot.utils.DateUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TaskBilibiliUpInfo {

    private static final UpInfoRepository upInfoRepository  = SpringUtils.getBean(UpInfoRepository.class);


    @Async("taskExecutor")
    @Scheduled(cron = "30 * * * * ?")
    public void newDynamic(){
        List<UpInfo> all = upInfoRepository.findAll();

        for (UpInfo upInfo : all){
            Dynamic newDynamic = BilibiliUtils.getUpNewDynamic(upInfo.getMId());
            if (newDynamic == null) {
                log.warn("["+ upInfo.getMId() +"] - 动态获取异常");
                break;
            }

            Dynamic oldDynamic = upInfo.getDynamic();
            if (oldDynamic == null) {
                BilibiliCodes.newDynamic(upInfo , newDynamic);
                return;
            }
            if (oldDynamic.getDynamicTime().before(newDynamic.getDynamicTime())){
                BilibiliCodes.newDynamic(upInfo , newDynamic);
            }
        }
    }

}
