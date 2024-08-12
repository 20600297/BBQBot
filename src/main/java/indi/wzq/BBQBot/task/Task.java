package indi.wzq.BBQBot.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Task {

    /**
     * 存活日志
     */
    @Async("taskExecutor")
    @Scheduled(cron = "0 0 * * * ?")
    public void life(){
        log.info("-----当前程序正常存活！-----");
    }

}
