package indi.wzq.BBQBot.plugin.group;

import com.alibaba.fastjson2.JSONObject;
import indi.wzq.BBQBot.utils.FileUtils;
import indi.wzq.BBQBot.utils.Graphic.GraphicUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.Random;

public class TarotMaster {

    private static final String TAROT_JSON_PATH = "static/json/tarot/tarot.json";
    private static final String TAROT_IMG_PATH = "static/img/tarot/";

    /**
     * 抽取N张塔罗牌
     * @param num 抽取数量
     * @return [num][牌名，顺逆，信息体]
     */
    public static String[][] getTarots(Integer num){
        String[][] data = new String[num][3];
        for (int i = 0 ; i < num ; i++){
            // 随机牌号
            Random random = new Random();
            String sn = String.valueOf(random.nextInt(77));

            // 获取牌名
            String tarotJson = FileUtils.readJsonByClasspath(TAROT_JSON_PATH);
            JSONObject card = JSONObject.parseObject(tarotJson).getJSONObject("cards").getJSONObject(sn);
            String cardPic = card.getString("pic").trim();
            String cardName = card.getString("name_cn").trim();

            // 获取img字节组
            byte[] imgBytes = FileUtils.readImgByClasspath(TAROT_IMG_PATH + cardPic + ".png");

            // 确定顺逆
            int direction = random.nextInt(2);
            if (direction == 1)
                imgBytes = GraphicUtils.ImageRotate180(imgBytes);

            // 构建消息
            String msg = Msg.builder()
                    .imgBase64(imgBytes)
                    .build();

            // 构建返回
            data[i][0] = cardName;
            data[i][1] = String.valueOf(direction);
            data[i][2] = msg;
        }
        return data;
    }


}
