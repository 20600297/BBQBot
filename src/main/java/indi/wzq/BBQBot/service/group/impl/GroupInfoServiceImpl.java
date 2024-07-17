package indi.wzq.BBQBot.service.group.impl;

import indi.wzq.BBQBot.entity.group.GroupInfo;
import indi.wzq.BBQBot.repo.GroupInfoRepository;
import indi.wzq.BBQBot.service.group.GroupInfoService;
import indi.wzq.BBQBot.utils.SpringUtils;
import org.springframework.stereotype.Service;

@Service
public class GroupInfoServiceImpl implements GroupInfoService {

    private static final GroupInfoRepository groupInfoRepository = SpringUtils.getBean(GroupInfoRepository.class);

    /**
     * 构建群信息
     * @param group_id 群id
     * @param bot_id botId
     */
    @Override
    public void creatGroupInfo(Long group_id, Long bot_id) {
        GroupInfo groupInfo = groupInfoRepository.findGroupInfoByGroupIdAndBotId(group_id, bot_id);

        if (groupInfo == null) {
            groupInfo =new GroupInfo(group_id,bot_id);
        }
        groupInfoRepository.save(groupInfo);
    }

    /**
     * 通过 群id 获取 botId
     * @param group_id 群id
     * @return botId
     */
    @Override
    public long findBotIdByGroupId(long group_id) {
        return groupInfoRepository.findBotIdByGroupId(group_id);
    }
}
