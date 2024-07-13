package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.group.LiveSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LiveSubscribeRepository extends JpaRepository<LiveSubscribe,Long> , JpaSpecificationExecutor<LiveSubscribe> {

}
