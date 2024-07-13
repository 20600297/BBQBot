package indi.wzq.BBQBot.entity.bilibili;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LiveInfo {


    // 房间id
    @Id
    String roomId;

    // 主播昵称
    String uname;

    // 直播间标题
    String title;

    // 直播间封面
    String cover;

    // 直播间状态
    Integer status;

}
