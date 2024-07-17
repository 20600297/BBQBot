package indi.wzq.BBQBot.entity.group;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GroupTask {

    @Id
    Long groupId;

    Boolean dailyNews;

    public GroupTask(Long group_id) {
        this.groupId = group_id;
        this.dailyNews = false;
    }
}
