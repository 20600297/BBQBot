package indi.wzq.BBQBot.entity.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UpSubscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    Long eid;

    // 订阅的Bot的id
    Long botId;

    // 订阅的群id
    Long groupId;

    // up的mid
    String mid;

    public UpSubscribe(long botId, long groupId, String mId){
        this.botId = botId;
        this.groupId =  groupId;
        this.mid = mId;
    }
}
