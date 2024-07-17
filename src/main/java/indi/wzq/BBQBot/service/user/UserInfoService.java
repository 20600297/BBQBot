package indi.wzq.BBQBot.service.user;

import indi.wzq.BBQBot.entity.group.UserInfo;

public interface UserInfoService {

    /**
     * 通过用户id获取用户信息
     * @param user_id 用户id
     * @return 用户信息
     */
    UserInfo findUserInfoByUserId(Long user_id);

    /**
     * 储存用户信息
     * @param userInfo 用户信息
     */
    void saveUserInfo(UserInfo userInfo);
}
