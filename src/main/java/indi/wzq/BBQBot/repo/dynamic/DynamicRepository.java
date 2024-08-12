package indi.wzq.BBQBot.repo.dynamic;

import indi.wzq.BBQBot.entity.bilibili.Dynamic.Dynamic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DynamicRepository extends JpaRepository<Dynamic,Long>, JpaSpecificationExecutor<Dynamic> {
}
