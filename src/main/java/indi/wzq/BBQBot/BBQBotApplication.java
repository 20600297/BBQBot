package indi.wzq.BBQBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BBQBotApplication {
    public static void main(String[] args){
        SpringApplication.run(BBQBotApplication.class,args);
    }
}
