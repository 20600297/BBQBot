package indi.wzq.BBQBot.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GraphicUtils {

    private static final Font customFont = getFontByClasspath("/static/font/MaoKenYuanZhuTi.ttf");

    /**
     * 绘制签到返回图像
     * @param background_url 背景图片
     * @param user_face_url 头像图片
     * @param user_name 用户昵称
     * @return 图像流
     */
    public static BufferedImage graphicSignInMsg(String background_url,String user_face_url,String user_name){
        try {
            // 可以选择设置字体的大小等属性
            Font finalFont = customFont.deriveFont(40f); // 字体大小设置为40

            // 背景图像
            File backgroundImageFile = new File(background_url);
            BufferedImage backgroundImage = ImageIO.read(backgroundImageFile);

            // 用户头像
            File faceImageFile = new File(user_face_url);
            BufferedImage faceImage = ImageIO.read(faceImageFile);

            // 将头像处理为圆形
            faceImage = clipFaceCircle(faceImage);

            // 将背景图像缩小至宽度为 1280
            backgroundImage = resizeImage(backgroundImage,1280);

            // 生成绘画对象
            Graphics2D g2d = backgroundImage.createGraphics();

            // 设置透明度（Alpha值为0.5表示半透明）
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            FontRenderContext frc = g2d.getFontRenderContext();
            Rectangle2D bounds = finalFont.getStringBounds(user_name, frc);

            // 绘制用户信息栏
            RoundRectangle2D roundRect = new RoundRectangle2D.Double(30, 60, 175+bounds.getWidth(), 100, 8, 8);
            g2d.setColor(Color.WHITE); // 设置颜色为白色
            g2d.fill(roundRect); // 填充圆角矩形

            // 绘制底部时间栏
            roundRect = new RoundRectangle2D.Double(1090,backgroundImage.getHeight() - 30 , 180, 20, 8, 8);
            g2d.setColor(Color.WHITE); // 设置颜色为白色
            g2d.fill(roundRect); // 填充圆角矩形

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            g2d.drawImage(faceImage,50,45,null);

            // 设置文本的颜色
            g2d.setColor(Color.BLACK); // 或者你想要的任何颜色
            g2d.setFont(finalFont);

            // 绘制文本
            g2d.drawString(user_name, 175, 105);

            g2d.drawString(DateUtils.getGreeting(), 175, 145);

            writeCopyright(backgroundImage, "Creat By BBQBot v1.0.4");

            writeDate(backgroundImage,DateUtils.format(new Date(),"yyyy-MM-dd hh:mm:ss"));

            // 释放Graphics2D资源
            g2d.dispose();

            return backgroundImage;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 绘制今日运势
     * @param background_url 背景图片路径
     * @return 图像流
     */
    public static BufferedImage graphicFortune(String background_url){
        try {
            BufferedImage backgroundImage = ImageIO
                    .read(new ByteArrayInputStream(FileUtils.readImageFile(background_url)));

            // 生成绘画对象
            Graphics2D g2d = backgroundImage.createGraphics();

            Font finalFont = customFont.deriveFont(40f); // 字体大小设置为40
            g2d.setFont(finalFont);
            g2d.setColor(Color.WHITE); // 设置颜色为白色

            List<String> fortune = getFortune();
            String luck = fortune.get(0);
            String content = fortune.get(1);


            FontRenderContext frc = g2d.getFontRenderContext();
            Rectangle2D bounds = finalFont.getStringBounds(luck, frc);
            int x = (int) (280 - bounds.getWidth()) / 2;
            g2d.drawString(luck, x, 115);

            writeContest(g2d,content);

            // 释放Graphics2D资源
            g2d.dispose();

            return backgroundImage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将QQ头像处理为圆形
     * @param original_image 原始图像
     * @return 处理后的图像实例
     */
    private static BufferedImage clipFaceCircle(BufferedImage original_image ){

        int width = original_image .getWidth();
        int height = original_image .getHeight();
        if (width != 100 || height != 100) {
            throw new IllegalArgumentException("Image must be 1080x1080");
        }

        // 创建一个新的BufferedImage来存储圆形图片
        BufferedImage circleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 绘制圆形到新的BufferedImage中，但这里我们不直接使用Graphics2D绘制，而是遍历像素
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 检查当前像素是否在圆形内
                // 使用勾股定理计算点到圆心的距离，如果小于等于半径，则认为在圆内
                if (isInsideCircle(x, y, width / 2, height / 2, width / 2)) {
                    // 在圆内，保留像素
                    circleImage.setRGB(x, y, original_image.getRGB(x, y));
                } else {
                    // 不在圆内，设置为透明（或任何你想要的背景色）
                    circleImage.setRGB(x, y, 0); // 0 是完全透明的ARGB值
                }
            }
        }
        return circleImage;
    }

    /**
     * 检查点(x, y)是否在以(centerX, centerY)为中心，radius为半径的圆内
     * @param x x
     * @param y y
     * @param centerX 圆心x
     * @param centerY 圆心y
     * @param radius 半径
     * @return 布尔值
     */
    private static boolean isInsideCircle(int x, int y, int centerX, int centerY, int radius) {
        return (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) <= radius * radius;
    }


    /**
     * 将图像缩小
     * @param original_image 原始图像
     * @param target_width 目标宽度
     * @return 缩小后的图像
     */
    private static BufferedImage resizeImage(BufferedImage original_image, int target_width){
        int originalWidth = original_image.getWidth();
        int originalHeight = original_image.getHeight();

        double scaleFactor = (double) target_width / originalWidth;

        int target_height = (int) (originalHeight * scaleFactor);

        BufferedImage resizedImage = new BufferedImage(target_width, target_height, original_image.getType());
        Graphics2D g2d = resizedImage.createGraphics();

        // 设置高质量的插值方法
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // 绘制缩放后的图像
        g2d.drawImage(original_image, 0, 0, target_width, target_height, null);
        g2d.dispose();

        return resizedImage;
    }


    /**
     * 绘制版权
     *
     * @param original_image 原始图像
     * @param copyright      版权信息
     */
    private static void writeCopyright(BufferedImage original_image, String copyright){
        Graphics2D g2d = original_image.createGraphics();

        Font finalFont = new Font("Arial", Font.ITALIC, 16); // 字体名，样式，大小
        g2d.setFont(finalFont);

        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = finalFont.getStringBounds(copyright, frc);

        // 计算水平居中的x坐标
        int x = (int) (original_image.getWidth() - bounds.getWidth()) / 2;

        int y = getBottomFountY(original_image, finalFont, frc, copyright);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(x-3,y - bounds.getHeight(), bounds.getWidth()+6, bounds.getHeight()+6, 8, 8);
        g2d.setColor(Color.WHITE); // 设置颜色为白色
        g2d.fill(roundRect); // 填充圆角矩形
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g2d.setColor(Color.BLACK); // 或者你想要的任何颜色
        g2d.drawString(copyright, x, y);
    }

    /**
     * 绘制制作时间
     * @param original_image 原始图像
     * @param date 时间字符串
     */
    private static void writeDate(BufferedImage original_image, String date){
        Graphics2D g2d = original_image.createGraphics();

        // 设置文本的颜色
        g2d.setColor(Color.BLACK); // 或者你想要的任何颜色

        Font finalFont = new Font("Arial", Font.BOLD, 16); // 字体名，样式，大小
        g2d.setFont(finalFont);

        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = finalFont.getStringBounds(date, frc);

        int x = (int) (original_image.getWidth() - bounds.getWidth() - 20);

        // 注意：LineMetrics提供了对字体行高的更精确控制
        int y = getBottomFountY(original_image, finalFont, frc, date);

        g2d.drawString(date, x, y);
    }

    /**
     * 获取底部文字绘制y坐标
     * @param original_image 原始图像
     * @param finalFont 字体
     * @param frc 绘制
     * @param str 内容
     * @return y值
     */
    private static int getBottomFountY(BufferedImage original_image, Font finalFont ,FontRenderContext frc, String str){
        LineMetrics metrics = finalFont.getLineMetrics(str, frc);
        float ascent = metrics.getAscent(); // 文本基线以上的部分
        float descent = metrics.getDescent(); // 文本基线以下的部分
        float leading = metrics.getLeading(); // 行间距
        float totalHeight = ascent + descent + leading;
        return original_image.getHeight() - (int) (totalHeight) + 5;
    }

    /**
     * 通过字体文件类路径加载字体
     * @param path 字体文件类路径
     * @return 字体文件
     */
    private static Font getFontByClasspath(String path){
        try {

            InputStream inputStream = GraphicUtils.class.getResourceAsStream(path);

            if (inputStream == null) {
                return new Font("Arial", Font.BOLD, 16);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            inputStream.close();

            return font;
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载运势文案
     * @return 运势文案
     */
    private static List<String> getFortune(){
        InputStream inputStream = GraphicUtils.class
                .getClassLoader()
                .getResourceAsStream("static/fortune/copywriting.json");

        if (inputStream == null) {
            throw new RuntimeException("资源文件未找到: static/fortune/copywriting.json");
        }

        // 使用BufferedReader来读取InputStream中的内容
        StringBuilder jsonStringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 现在jsonStringBuilder中包含了完整的JSON字符串
        String jsonString = jsonStringBuilder.toString().trim();

        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        JSONArray copywriting = jsonObject.getJSONArray("copywriting");

        Random random = new Random();

        JSONObject fortune = copywriting.getJSONObject(random.nextInt(copywriting.size()));

        String good_luck = fortune.getString("good-luck");

        JSONArray contents = fortune.getJSONArray("content");

        String content = contents.getString(random.nextInt(contents.size()));
        return  List.of(good_luck,content);
    }


    private static void writeContest(Graphics2D g2d,String contest){
        Font finalFont = getFontByClasspath("/static/font/sakura.ttf").deriveFont(18f);
        g2d.setFont(finalFont);
        g2d.setColor(Color.BLACK);

        FontRenderContext frc = g2d.getFontRenderContext();

        if (!contest.matches(".*[ ,!].*")){
            for (int i = 0; i < contest.length(); i++) {
                String substring = contest.substring(i, i + 1);

                Rectangle2D bounds = finalFont.getStringBounds(substring, frc);
                int x = (int)( ((280 - bounds.getWidth()) / 2) );
                int y = (int)( 190 + i * bounds.getHeight() );
                g2d.drawString(substring, x, y);
            }
        } else {
            int i = 0;
            for (; i < contest.length(); i++) {
                String substring = contest.substring(i, i + 1);

                Rectangle2D bounds = finalFont.getStringBounds(substring, frc);
                int x = (int)( ((280 - bounds.getWidth()) / 2) + 20);
                int y = (int)( 190 + i * bounds.getHeight() );
                g2d.drawString(substring, x, y);
                if (substring.matches(".*[ ,!].*")) break;
            }

            for (int j = 0; i < contest.length(); i++,j++) {
                String substring = contest.substring(i, i + 1);

                Rectangle2D bounds = finalFont.getStringBounds(substring, frc);
                int x = (int)( ((280 - bounds.getWidth()) / 2) - (bounds.getWidth()+5) );
                int y = (int)( 190 + (j) * bounds.getHeight());
                g2d.drawString(substring, x, y);
            }
        }



    }

    /**
     * 判断背景图片是否合适
     * @param image_path 本地路径
     * @return 布尔值
     */
    public static boolean isSuitable(String image_path) {
        try {
            // 使用ImageIO读取图片文件
            File file = new File(image_path);
            BufferedImage image = ImageIO.read(file);

            // 获取图片的宽度
            int width = image.getWidth();

            return 1280 < width & width < 4000;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
