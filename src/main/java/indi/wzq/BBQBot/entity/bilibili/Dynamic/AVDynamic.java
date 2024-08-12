package indi.wzq.BBQBot.entity.bilibili.Dynamic;

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

}
