package indi.wzq.BBQBot.utils;

import com.alibaba.fastjson2.JSONObject;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.utils.http.HttpUtils;

public class BilibiliUtils {

    //TODO:Bilibili信息获取代码优化


    /***
     * 通过房间id获取直播间信息
     * @param room_id 房间id
     * @return 直播间信息类实例
     */
    public static LiveInfo getLiveInfoByRoomId(String room_id){
        HttpUtils.Body body = HttpUtils.sendGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom", "room_id=" + room_id);

        if (JSONObject.parseObject(body.getBody()).getInteger("code") == 0) {
            JSONObject data = JSONObject.parseObject(body.getBody()).getJSONObject("data");

            String uname = data.getJSONObject("anchor_info").getJSONObject("base_info").getString("uname");

            String title = data.getJSONObject("room_info").getString("title");

            String cover = data.getJSONObject("room_info").getString("cover");

            Integer status = data.getJSONObject("room_info").getInteger("live_status");

            return new LiveInfo(room_id,uname,title,cover,status);
        } else {
            System.out.println(room_id + "：直播间状态异常");
            return null;
        }
    }

    /***
     * 通过房间id获取直播间状态码
     * @param room_id 房间id
     * @return 直播间状态码
     */
    public static Integer getLiveStatusByRoomId(String room_id){

        HttpUtils.Body body = HttpUtils.sendGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom", "room_id=" + room_id);

        if (JSONObject.parseObject(body.getBody()).getInteger("code") == 0) {

            return JSONObject.parseObject(body.getBody()).getJSONObject("data")
                    .getJSONObject("room_info").getInteger("live_status");
        } else {

            System.out.println(room_id + "：直播间状态异常");
            return -2;

        }
    }

}
