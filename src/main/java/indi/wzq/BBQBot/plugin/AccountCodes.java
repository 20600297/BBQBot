package indi.wzq.BBQBot.plugin;

import com.alibaba.fastjson2.JSONObject;
import com.google.zxing.WriterException;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import indi.wzq.BBQBot.enums.API;
import indi.wzq.BBQBot.utils.FileUtils;
import indi.wzq.BBQBot.utils.QrCodeUtils;
import indi.wzq.BBQBot.utils.http.HttpUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;
import okhttp3.Headers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountCodes {

    /**
     * 登陆B站账号事件
     * @param bot Bot
     * @param event Event
     */
    public static void BilibiliLogin(Bot bot, AnyMessageEvent event) {
        // 阻止群组触发
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return;
        }

        // 启动线程
        Thread pollingThread = new Thread(() -> {

            // 获取 url 和 key
            HttpUtils.Body body = HttpUtils
                    .sendGet(API.BILIBILI_QRCODE_GENERATE.getUrl());

            JSONObject data = JSONObject.parseObject(body.getBody()).getJSONObject("data");
            String url = data.getString("url");
            String qrcode_key = data.getString("qrcode_key");

            try {
                // 生成 二维码
                byte[] bytes = QrCodeUtils.generateQRCode(url, 300, 300);

                // 发送 二维码
                String msg = Msg.builder()
                        .imgBase64(bytes)
                        .build();
                bot.sendMsg(event, msg, false);

                // 校验 二维码状态
                while (true) {
                    Thread.sleep(5000);
                    body = HttpUtils
                            .sendGet(API.BILIBILI_QRCODE_POLL.getUrl()
                                    , "qrcode_key=" + qrcode_key);
                    data = JSONObject.parseObject(body.getBody()).getJSONObject("data");

                    switch (data.getInteger("code")) {
                        // 登陆成功
                        case 0 -> {
                            // 解析cookies
                            Headers headers = body.getHeaders();
                            Map<String,String> cookies = new HashMap<>();
                            List<String> list = headers.toMultimap().get("set-cookie");
                            for (String s : list){
                                String[] cookie = s.split(";")[0].split("=");
                                cookies.put(cookie[0],cookie[1]);
                            }

                            // 将cookies写入json文件
                            FileUtils.saveCookieMap(cookies,"./data/cookies.json");

                            return;
                        }
                        // 二维码失效
                        case 86038 -> {
                            bot.sendMsg(event, "已经失效", false);
                            return;
                        }
                    }

                }

            } catch (WriterException | IOException | InterruptedException e) {
                bot.sendMsg(event, "图片生成异常！", false);
                throw new RuntimeException(e);
            }

        });

        // 停止线程
        pollingThread.start();
    }
}