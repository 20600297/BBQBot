package indi.wzq.BBQBot.entity.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * 订阅信息类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LiveSubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    Long eid;

    // 订阅的Bot的id
    long botId;

    // 订阅的群id
    long groupId;

    // 直播间id
    String roomId;

    public LiveSubscribe(long botId, long groupId, String roomId){
        this.botId = botId;
        this.groupId =  groupId;
        this.roomId = roomId;
    }
}
