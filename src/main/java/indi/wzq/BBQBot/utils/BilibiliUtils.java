package indi.wzq.BBQBot.utils;

import com.alibaba.fastjson2.JSONObject;
import indi.wzq.BBQBot.entity.bilibili.LiveInfo;
import indi.wzq.BBQBot.utils.http.HttpUtils;

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
     * 通过房间id获取直播间状态码
     * @param room_id 房间id
     * @return 直播间状态码
     */
    public static Integer getLiveStatusByRoomId(String room_id){

        HttpUtils.Body body = HttpUtils.sendGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom", "room_id=" + room_id);
        JSONObject jsonObject = JSONObject.parseObject(body.getBody());
        if (jsonObject.getInteger("code") == 0) {

            return jsonObject.getJSONObject("data")
                    .getJSONObject("room_info").getInteger("live_status");
        } else {

            System.out.println(room_id + "：直播间状态异常");
            return -2;

        }
    }


    /**
     * 通过房间id获取开播时间
     * @param room_id 房间id
     * @return 开播时间戳
     */
    public static Long getStartTimeByRoomId(String room_id){

        HttpUtils.Body body = HttpUtils.sendGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom", "room_id=" + room_id);
        JSONObject jsonObject = JSONObject.parseObject(body.getBody());
        if (jsonObject.getInteger("code") == 0) {

            return jsonObject.getJSONObject("data")
                    .getJSONObject("room_info").getLong("live_start_time");
        } else {

            System.out.println(room_id + "：直播间状态异常");
            return 0L;

        }
    }

}
