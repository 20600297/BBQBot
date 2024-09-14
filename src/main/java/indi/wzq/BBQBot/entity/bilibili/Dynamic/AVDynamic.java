package indi.wzq.BBQBot.entity.bilibili.Dynamic;

import indi.wzq.BBQBot.utils.onebot.Msg;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AVDynamic extends Dynamic {

    // 视频封面
    String cover;

    // 视频标题
    String title;

    public AVDynamic(String id, Date time, String type, String jump_url, String cover, String title){
        this.dynamicId = id;
        this.dynamicTime = time;
        this.type = type;
        this.jumpUrl = jump_url;
        this.cover = cover;
        this.title = title;
    }

    public String newMsg(){
        return Msg.builder()
                .text("发布了新的视频！\r\n")
                .img(cover)
                .text("\r\n《"+ title + "》\r\n")
                .text("快去围观吧！\r\n")
                .text(jumpUrl)
                .build();
    }

}
