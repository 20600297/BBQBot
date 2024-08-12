package indi.wzq.BBQBot.utils;

import com.alibaba.fastjson2.JSONObject;
import indi.wzq.BBQBot.entity.bilibili.Dynamic.AVDynamic;
import indi.wzq.BBQBot.entity.bilibili.Dynamic.Dynamic;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.entity.bilibili.UpInfo;
import indi.wzq.BBQBot.enums.API;
import indi.wzq.BBQBot.repo.dynamic.AVDynamicRepository;
import indi.wzq.BBQBot.repo.dynamic.DynamicRepository;
import indi.wzq.BBQBot.utils.http.HttpUtils;
import okhttp3.Headers;

import java.util.Date;
import java.util.Map;

public class BilibiliUtils {

    /**
     * 通过房间id获取直播间信息
     * @param room_id 房间id
     * @return 直播间信息类实例
     */
    public static LiveInfo getLiveInfoByRoomId(String room_id){
        HttpUtils.Body body = HttpUtils.sendGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom", "room_id=" + room_id);

        JSONObject jsonObject = JSONObject.parseObject(body.getBody());

        if (jsonObject.getInteger("code") == 0) {

            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject base_info = data.getJSONObject("anchor_info").getJSONObject("base_info");
            JSONObject room_info = data.getJSONObject("room_info");

            String uname = base_info.getString("uname");
            String face = base_info.getString("face");
            String title = room_info.getString("title");
            String cover = room_info.getString("cover");
            Integer live_status = room_info.getInteger("live_status");
            Long live_start_time = room_info.getLong("live_start_time");
            return new LiveInfo(room_id,uname,face,title,cover,live_status,live_start_time);
        } else {
            System.out.println(room_id + "：直播间状态异常");
            return null;
        }
    }

    /**
     * 通过UP的mid获取用户信息
     * @param mid mid
     * @return UpInfo
     */
    public static UpInfo getUpInfoByUID(String mid){
        // 获取cookies
        String cookie = FileUtils.readCookie("./data/cookies.json");
        if (cookie == null) {
            return null;
        }

        HttpUtils.Body body = HttpUtils.sendGet(API.BILIBILI_GET_USER_CARD.getUrl(),
                "mid="+mid,
                Headers.of("Cookie",cookie).newBuilder());

        JSONObject card = JSONObject.parseObject(body.getBody()).getJSONObject("data").getJSONObject("card");

        return new UpInfo(mid, card.getString("name"), card.getString("face"), null);
    }

    /**
     * 获取UP最新动态
     * @param mid UP的mid
     * @return 最新动态信息
     */
    public static Dynamic getUpNewDynamic(String mid){
        // 获取cookies
        String cookie = FileUtils.readCookie("./data/cookies.json");
        if (cookie == null) {
            return null;
        }

        HttpUtils.Body body = HttpUtils.sendGet(
                API.BILIBILI_GET_DYNAMIC_SPACE.getUrl(),
                "host_mid=" + mid,
                Headers.of("Cookie",cookie).newBuilder());

        return disposeSpaceDynamics(JSONObject.parseObject(body.getBody()));
    }

    /**
     * 解析动态列表Json
     * @param space_json 动态列表json
     * @return 第一个动态对象
     */
    private static Dynamic disposeSpaceDynamics(JSONObject space_json){

        JSONObject firstDynamic = space_json.getJSONObject("data").getJSONArray("items").getJSONObject(0);

        if (firstDynamic == null) {
            return null;
        }

        String id = firstDynamic.getString("id_str");
        String type = firstDynamic.getString("type");

        JSONObject modules = firstDynamic.getJSONObject("modules");

        long time = modules.getJSONObject("module_author").getLong("pub_ts") * 1000;
        Date date = new Date(time);

        switch (type){
            case "DYNAMIC_TYPE_AV" ->{
                // 发布视频
                JSONObject archive = modules.getJSONObject("module_dynamic")
                        .getJSONObject("major")
                        .getJSONObject("archive");

                String cover = archive.getString("cover");
                String jumpUrl = archive.getString("jump_url").substring(2);
                String title = archive.getString("title");
                AVDynamic avDynamic = new AVDynamic(id, date, type, jumpUrl, cover, title);

                SpringUtils.getBean(AVDynamicRepository.class).save(avDynamic);
                return avDynamic;
            }
            case "DYNAMIC_TYPE_DRAW" -> {
                // 图文动态
                String jumpUrl = "www.bilibili.com/opus/" + id;

                Dynamic dynamic = new Dynamic(id, date, type, jumpUrl);

                SpringUtils.getBean(DynamicRepository.class).save(dynamic);
                return dynamic;
            }
            default -> {
                Dynamic dynamic = new Dynamic(id, date, type, null);

                SpringUtils.getBean(DynamicRepository.class).save(dynamic);
                return dynamic;
            }
        }
    }

}
