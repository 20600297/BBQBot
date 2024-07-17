package indi.wzq.BBQBot.service.group;

public interface GroupInfoService {

    /**
     * 构建群信息
     * @param group_id 群id
     * @param bot_id botId
     */
    void creatGroupInfo(Long group_id, Long bot_id);

    /**
     * 通过 群id 获取 botId
     * @param group_id 群id
     * @return botId
     */
    long findBotIdByGroupId(long group_id);
}
