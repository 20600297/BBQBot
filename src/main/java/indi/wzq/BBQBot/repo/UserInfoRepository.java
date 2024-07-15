package indi.wzq.BBQBot.repo;

import indi.wzq.BBQBot.entity.group.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserInfoRepository extends JpaRepository<UserInfo,Long>, JpaSpecificationExecutor<UserInfo> {

    UserInfo findByUserId(long user_id);

}
