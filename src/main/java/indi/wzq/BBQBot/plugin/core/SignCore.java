package indi.wzq.BBQBot.plugin.core;

import indi.wzq.BBQBot.entity.group.UserInfo;
import indi.wzq.BBQBot.repo.UserInfoRepository;
import indi.wzq.BBQBot.utils.DateUtils;
import indi.wzq.BBQBot.utils.FileUtils;
import indi.wzq.BBQBot.utils.Graphic.GraphicUtils;
import indi.wzq.BBQBot.utils.SpringUtils;
import indi.wzq.BBQBot.utils.http.HttpUtils;
import indi.wzq.BBQBot.utils.onebot.Msg;
import okhttp3.Headers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public class SignCore {

    /**
     * 签到信息返回
     * @param user_id QQ号
     * @param user_name QQ昵称
     * @param message_id 消息id
     * @return 签到返回信息
     */
    public static String[] getSignInMsg( long user_id , String user_name , Integer message_id){

        // 通过签到用户id获取用户信息
        UserInfo user_info = SpringUtils.getBean(UserInfoRepository.class).findByUserId(user_id);

        // 如果未获得到用户信息则初始化用户信息
        if (user_info == null)
            user_info = new UserInfo(user_id);

        // 获取当前时间
        Date sign_time = new Date();

        // 判断今日是否签到
        if ( DateUtils.isSameDay(sign_time,user_info.getSignInTime()) ){
            String[] msgs = new String[1];
            msgs[0] = Msg.builder().reply(message_id)
                    .at(user_info.getUserId())
                    .text("您今天已经签过到了呢！")
                    .build();
            // 构建消息
            return msgs;
        }

        // 判断是否为连续签到
        if (DateUtils.isYesterdayOrEarlier(user_info.getSignInTime(),sign_time)){
            user_info.setSignInContNum(user_info.getSignInContNum() + 1);
        } else {
            user_info.setSignInContNum(1);
        }

        user_info.setSignInTime(sign_time);
        user_info.setSignInNum(user_info.getSignInNum() + 1 );

        // 更新用户信息
        SpringUtils.getBean(UserInfoRepository.class).save(user_info);

        String[] msgs = new String[2];
        msgs[0] = Msg.builder().reply(message_id)
                .at(user_info.getUserId())
                .text(" 成功签到-")
                .text(DateUtils.format(user_info.getSignInTime(),"yyyy/MM/dd") + "\t\n")
                .text("签到次数 %d；连续签到 %d。".formatted(user_info.getSignInNum(),user_info.getSignInContNum()))
                .build();
        msgs[1] = getSignInImgMsg(user_info.getUserId(),user_name);
        return msgs;
    }

    /**
     * 签到图返回
     * @param user_id 用户id
     * @param user_name 用户名
     * @return 签到返回图
     */
    public static String getSignInImgMsg(Long user_id , String user_name){

        // 获取背景图片
        byte[] background = getBackground();

        // 获取QQ头像
        HttpUtils.Body face = HttpUtils.sendGetFile("https://qlogo2.store.qq.com/qzone/"+user_id+"/"+user_id+"/100");

        // 绘图
        try {

            // 初始化图像
            BufferedImage backgroundImage = ImageIO.read(
                    new ByteArrayInputStream(background)
            );
            BufferedImage faceImage = ImageIO.read(
                    new ByteArrayInputStream(face.getFile())
            );

            // 将QQ头像剪切为圆形
            faceImage = GraphicUtils.clipCircle(faceImage);

            // 将背景图缩小
            backgroundImage = GraphicUtils.resizeImage(backgroundImage,1280);

            // 将信息绘制再背景图上
            BufferedImage image = GraphicUtils.graphicSignInMsg(backgroundImage, faceImage, user_name);

            return Msg.builder()
                    .imgBase64(FileUtils.bufferedImage2Bytes(image))
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 获取背景图片
     * @return 字节组
     */
    private static byte[] getBackground(){
        // 获取签到背景图
        HttpUtils.Body background = HttpUtils.sendGetFile("https://iw233.cn/api.php"
                ,"sort=pc"
                , Headers.of("referer", "https://weibo.com/").newBuilder());

        String backgroundName = DateUtils.format(new Date(), "yyyy-MM-dd-HH_mm_ss") + "-bg.png";

        byte[] bytes = background.getFile();

        FileUtils.saveImageFile( bytes, "./collection/img/Background/", backgroundName);

        // 判断比例是否合适
        if ( !GraphicUtils.isSuitable(bytes) ){
            return getBackground();
        }

        return bytes;
    }

    /**
     * @return  帮助文本
     */
    public static String getHelp(){

        return Msg.builder()
                .text("--- 签到 ---\r\n")
                .text("指令：“签到”\r\n")
                .text("功能：签到\r\n")
                .text("-----------")
                .build();

    }

}
