package indi.wzq.BBQBot.plugin;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.GroupAddRequestHandler;
import com.mikuac.shiro.annotation.GroupDecreaseHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import indi.wzq.BBQBot.enums.Codes;
import indi.wzq.BBQBot.permissions.Permissions;
import indi.wzq.BBQBot.plugin.code.AccountCodes;
import indi.wzq.BBQBot.plugin.code.BilibiliCodes;
import indi.wzq.BBQBot.plugin.code.GroupEvent;
import indi.wzq.BBQBot.plugin.code.GroupCodes;
import indi.wzq.BBQBot.utils.CodeUtils;
import indi.wzq.BBQBot.utils.onebot.CqMatcher;
import indi.wzq.BBQBot.utils.onebot.CqParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Shiro
@Component
@Slf4j
public class GlobalDirectivesPlugin {
    @AnyMessageHandler
    public void messageDisposeHandler(Bot bot, AnyMessageEvent event) {

        // 获取消息内容
        String raw = event.getRawMessage();

        // 判断是否是At
        if (CqMatcher.isCqAt(raw)) {
            CqParse build = CqParse.build(raw);
            if (build.getCqAt().get(0).equals(bot.getSelfId())) {
                raw = build.removeCq().trim();
            }
        }

        // 去除斜杠以及两端空格
        if (raw.contains("/")) {
            raw = raw.replaceAll("/", "").trim();
        }

        // 获取指令
        Codes code = CodeUtils.matchInstructions(raw);

        // 判断触发指令
        Optional.ofNullable(code).ifPresent(codes -> {

            // 权限校验
            if (Permissions.checkAdmin(bot, event).getI() < code.getPermissions().getI()){
                GroupEvent.unPermissions(bot,event);
                return;
            }

            switch (codes) {
                // 帮助
                case HELP -> GroupEvent.Help(bot,event);
                // 订阅直播间
                case SUBSCRIBE_LIVE -> BilibiliCodes.subscribeLive(bot,event);
                // 订阅UP主
                case SUBSCRIBE_UP -> BilibiliCodes.subscribeUp(bot,event);
                // 订阅每日早报
                case SUBSCRIBE_DAILYNEWS -> GroupCodes.subscribeDailyNews(bot,event);

                // 签到
                case GROUP_SIGNIN -> GroupCodes.signIn(bot,event);
                // 今日早报
                case GROUP_DAILYNEWS -> GroupCodes.todayNews(bot,event);
                // 今日运势
                case GROUP_FORTUNE -> GroupCodes.fortune(bot,event);

                // 抽塔罗牌
                case TAROT_GET_TAROT -> GroupCodes.getTarot(bot,event);
                // 抽N张塔罗牌
                case TAROT_GET_TAROTS -> GroupCodes.getTarots(bot,event);
                // 塔罗牌阵
                case TAROT_GET_FORMATIONS -> GroupCodes.getFormations(bot,event);

                // 登陆BiliBili
                case BILIBILI_ACCOUNT_LOGIN -> AccountCodes.BilibiliLogin(bot,event);
            }
        });
    }


    //TODO:入群申请校验及处理
    @GroupAddRequestHandler
    public void Add(Bot bot, GroupAddRequestEvent event){
        event.getComment();
        bot.setGroupAddRequest(event.getFlag(),event.getSubType(),true,"");
        GroupEvent.ConsentAdd(bot,event);
    }

    @GroupDecreaseHandler
    public void Decrease(Bot bot, GroupDecreaseNoticeEvent event){
        GroupEvent.Decrease(bot,event);
    }


}
