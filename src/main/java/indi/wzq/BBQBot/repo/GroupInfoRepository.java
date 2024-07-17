package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.group.GroupInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupInfoRepository  extends JpaRepository<GroupInfo,Long>, JpaSpecificationExecutor<GroupInfo> {

    GroupInfo findGroupInfoByGroupIdAndBotId(Long group_id, Long bot_id);

    @Query("SELECT g.botId FROM GroupInfo g WHERE g.groupId = :group_id")
    Long findBotIdByGroupId(@Param("group_id")  long group_id);

}
