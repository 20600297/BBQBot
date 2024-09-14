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
import indi.wzq.BBQBot.plugin.bilibili.BilibiliCodes;
import indi.wzq.BBQBot.plugin.group.GroupEvent;
import indi.wzq.BBQBot.plugin.group.GroupCodes;
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
            switch (codes) {
                case LIVE_SUBSCRIBE -> BilibiliCodes.LiveSubscribe(bot,event);
                case UP_SUBSCRIBE -> BilibiliCodes.UpSubscribe(bot,event);

                case GROUP_SIGNIN -> GroupCodes.signIn(bot,event);
                case GROUP_DAILYNEWS -> GroupCodes.DailyNews(bot,event);
                case GROUP_FORTUNE -> GroupCodes.Fortune(bot,event);
                case GROUP_SUBSCRIBE_DAILYNEWS -> GroupCodes.subscribeDailyNews(bot,event);

                case TAROT_GET_TAROT -> GroupCodes.getTarot(bot,event);
                case TAROT_GET_TAROTS -> GroupCodes.getTarots(bot,event);
                case TAROT_GET_FORMATIONS -> GroupCodes.getFormations(bot,event);

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
