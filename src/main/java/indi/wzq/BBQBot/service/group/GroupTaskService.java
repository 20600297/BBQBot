package indi.wzq.BBQBot.service.group;

import java.util.List;

public interface GroupTaskService {

    /**
     * 通过 群号 更改 每日早报订阅状态
     * @param group_id 群号
     * @param b 状态
     */
    void updateDailyNewsByGroupId(long group_id,boolean b);

    /**
     * 通过 每日早报订阅状态 获取 群号
     * @param b 订阅状态
     * @return 群号列表
     */
    List<Long> findGroupIdByDailyNews(boolean b);

}
