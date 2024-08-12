package indi.wzq.BBQBot.entity.bilibili.Dynamic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Dynamic {


    // 动态ID
    @Id
    String dynamicId;

    // 发布时间
    Date dynamicTime;

    // 动态类型
    String type;

    // 跳转链接
    String jumpUrl;

}
