package indi.wzq.BBQBot.entity.group;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserInfo {

    @Id
    Long userId;

    Date fortuneTime;

    Date signInTime;

    Integer signInNum;

    Integer signInContNum;

    Integer level;

    public UserInfo(long user_id){
        this.userId = user_id;
        this.fortuneTime = new Date(0);
        this.signInTime = new Date(0);
        this.signInNum = 0;
        this.signInContNum = 0;
        this.level = 1;
    }
}
