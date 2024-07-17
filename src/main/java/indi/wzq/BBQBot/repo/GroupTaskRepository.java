package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.group.GroupTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GroupTaskRepository extends JpaRepository<GroupTask,Long> , JpaSpecificationExecutor<GroupTask> {

    GroupTask findByGroupId(long group_id);

    @Query("SELECT g.groupId FROM GroupTask g WHERE g.dailyNews = :daily_news")
    List<Long> findGroupIdByDailyNews(@Param("daily_news")  boolean daily_news);
}
