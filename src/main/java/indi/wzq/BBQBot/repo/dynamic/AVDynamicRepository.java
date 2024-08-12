package indi.wzq.BBQBot.repo.dynamic;

import indi.wzq.BBQBot.entity.bilibili.Dynamic.AVDynamic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AVDynamicRepository extends JpaRepository<AVDynamic,Long>, JpaSpecificationExecutor<AVDynamic> {
}
