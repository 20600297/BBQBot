package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.bilibili.UpInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UpInfoRepository extends JpaRepository<UpInfo,String>, JpaSpecificationExecutor<UpInfo> {

    @Query("SELECT l.mid FROM UpInfo l")
    List<String> findAllMid();

    UpInfo findLiveByMid(String mid);


}
