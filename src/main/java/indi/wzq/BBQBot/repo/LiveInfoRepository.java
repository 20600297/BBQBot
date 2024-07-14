package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LiveInfoRepository extends JpaRepository<LiveInfo,String> , JpaSpecificationExecutor<LiveInfo> {

    LiveInfo findLiveByRoomId(String room_id);

}
