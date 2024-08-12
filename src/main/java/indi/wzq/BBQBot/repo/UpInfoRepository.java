package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.bilibili.UpInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UpInfoRepository extends JpaRepository<UpInfo,String>, JpaSpecificationExecutor<UpInfo> {

    UpInfo findByMId(String mid);

}
