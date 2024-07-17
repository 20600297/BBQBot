package indi.wzq.BBQBot.service.group.impl;

import indi.wzq.BBQBot.entity.group.GroupTask;
import indi.wzq.BBQBot.repo.GroupTaskRepository;
import indi.wzq.BBQBot.service.group.GroupTaskService;
import indi.wzq.BBQBot.utils.SpringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupTaskServiceImpl implements GroupTaskService {
    private static final GroupTaskRepository groupTaskRepository = SpringUtils.getBean(GroupTaskRepository.class);

    /**
     * 通过 群号 更改 每日早报订阅状态
     * @param group_id 群号
     * @param b 状态
     */
    @Override
    public void updateDailyNewsByGroupId(long group_id, boolean b) {
        GroupTask groupTask = groupTaskRepository.findByGroupId(group_id);
        if (groupTask == null){
            groupTask = new GroupTask(group_id);
        }

        groupTask.setDailyNews(b);

        groupTaskRepository.save(groupTask);
    }

    /**
     * 通过 每日早报订阅状态 获取 群号
     * @param b 订阅状态
     * @return 群号列表
     */
    @Override
    public List<Long> findGroupIdByDailyNews(boolean b) {
        return groupTaskRepository.findGroupIdByDailyNews(b);
    }
}
