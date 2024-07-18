package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.bilibili.LiveSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LiveSubscribeRepository extends JpaRepository<LiveSubscribe,Long> , JpaSpecificationExecutor<LiveSubscribe> {

    List<LiveSubscribe> findAllByRoomId(String room_id);

    boolean existsByGroupIdAndRoomId(long group_id, String room_id);

}
