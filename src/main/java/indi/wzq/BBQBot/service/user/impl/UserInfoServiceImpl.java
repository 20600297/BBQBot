package indi.wzq.BBQBot.service.user.impl;

import indi.wzq.BBQBot.entity.group.UserInfo;
import indi.wzq.BBQBot.repo.UserInfoRepository;
import indi.wzq.BBQBot.service.user.UserInfoService;
import indi.wzq.BBQBot.utils.SpringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final UserInfoRepository userInfoRepository = SpringUtils.getBean(UserInfoRepository.class);

    /**
     * 通过用户id获取用户信息
     * @param user_id 用户id
     * @return 用户信息
     */
    @Override
    public UserInfo findUserInfoByUserId(Long user_id) {
        return userInfoRepository.findByUserId(user_id);
    }

    /**
     * 储存用户信息
     * @param userInfo 用户信息
     */
    @Override
    public void saveUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }
}
