package indi.wzq.BBQBot.entity.bilibili;

import indi.wzq.BBQBot.entity.bilibili.Dynamic.Dynamic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UpInfo {

    // mid
    @Id
    String mId;

    // UP昵称
    String uname;

    // UP头像
    String face;

    // 最新动态ID
    @OneToOne
    Dynamic dynamic;

    public UpInfo(String mid){
        this.mId = mid;
        this.uname = "";
        this.face = "";
        this.dynamic = null;
    }
}
