package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.group.UpSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UpSubscribeRepository extends JpaRepository<UpSubscribe,String>, JpaSpecificationExecutor<UpSubscribe> {

    List<UpSubscribe> findAllByMid(String mid);

    boolean existsByGroupIdAndMid(Long groupId, String MId);
}
