package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LiveInfoRepository extends JpaRepository<LiveInfo,String> , JpaSpecificationExecutor<LiveInfo> {

    @Query("SELECT l.roomId FROM LiveInfo l")
    List<String> findAllRoomId();

    LiveInfo findLiveByRoomId(String room_id);

    @Query("SELECT l.status FROM LiveInfo l WHERE l.roomId = :room_id")
    Integer findStatusByRoomId(@Param("room_id") String room_id);

}
