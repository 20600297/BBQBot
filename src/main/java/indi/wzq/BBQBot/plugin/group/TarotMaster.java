package indi.wzq.BBQBot.plugin.group;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import indi.wzq.BBQBot.utils.FileUtils;
import indi.wzq.BBQBot.utils.Graphic.GraphicUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TarotMaster {

    private static final String TAROT_JSON_PATH = "static/json/tarot/tarot.json";
    private static final String TAROT_IMG_PATH = "static/img/tarot/";

    /**
     * 抽取N张塔罗牌
     *
     * @param num 抽取数量
     * @return [num][牌名，顺逆，信息体]
     */
    public static String[][] getTarots(Integer num){
        // 获取牌数据
        JSONObject tarotJson = JSONObject.parseObject(
                FileUtils.readJsonByClasspath(TAROT_JSON_PATH)
        );

        return getTarots(num,tarotJson);
    }

    /**
     * 抽取N张塔罗牌
     *
     * @param num       抽取数量
     * @param tarotJson 牌信息json
     * @return [num][牌名，顺逆，信息体]
     */
    public static String[][] getTarots(Integer num , JSONObject tarotJson){
        String[][] data = new String[num][4];
        for (int i = 0 ; i < num ; i++){
            // 随机牌号
            Random random = new Random();
            String sn = String.valueOf(random.nextInt(77));

            // 获取牌名
            JSONObject card = tarotJson.getJSONObject("cards").getJSONObject(sn);
            String cardPic = card.getString("pic").trim();
            String cardName = card.getString("name_cn").trim();

            // 获取img字节组
            byte[] imgBytes = FileUtils.readImgByClasspath(TAROT_IMG_PATH + cardPic + ".png");

            // 确定顺逆 获取解牌
            int direction = random.nextInt(2);
            String meaning;
            JSONObject meanings = card.getJSONObject("meaning");
            if (direction == 0) {
                meaning = meanings.getString("up");
            } else {
                imgBytes = GraphicUtils.ImageRotate180(imgBytes);

                meaning = meanings.getString("down");
            }


            // 构建消息
            String msg = Msg.builder()
                    .imgBase64(imgBytes)
                    .build();

            // 构建结果数据
            data[i][0] = cardName;
            data[i][1] = String.valueOf(direction);
            data[i][2] = msg;
            data[i][3] = meaning;
        }

        return data;
    }


    public static List<String> getFormations(String user_name , String formation_name){
        // 获取牌数据
        JSONObject tarotJson = JSONObject.parseObject(
                FileUtils.readJsonByClasspath(TAROT_JSON_PATH)
        );
        // 解析牌阵信息
        JSONObject formation = tarotJson.getJSONObject("formations").getJSONObject(formation_name);

        // 解析牌阵牌数
        Integer cardsNum = formation.getInteger("cards_num");
        // 获取牌信息
        String[][] tarots = getTarots(cardsNum, tarotJson);

        // 解析牌阵标签
        JSONArray representations = formation.getJSONArray("representations").getJSONArray(0);

        // 构建返回信息列表
        List<String> msgList = new ArrayList<>();

        msgList.add(user_name + " - ");
        msgList.add(formation_name + " - ");
        for (int i =0 ; i < cardsNum ;i++){
            if (tarots[i][1].equals("0")){
                msgList.add(representations.getString(i) + ":" + "【顺位】 的 【" + tarots[i][0] + "】");
            } else {
                msgList.add(representations.getString(i) + ":" + "【逆位】 的 【" + tarots[i][0] + "】");
            }
            msgList.add(tarots[i][2]);
            msgList.add("解牌：");
            msgList.add(tarots[i][3]);
        }

        return msgList;
    }
}
