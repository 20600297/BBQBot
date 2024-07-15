package indi.wzq.BBQBot.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class GraphicUtils {


    /**
     * 绘制签到返回图像
     * @param background_url 背景图片
     * @param user_face_url 头像图片
     * @param user_name 用户昵称
     * @return 图像流
     */
    public static BufferedImage graphicSignInMsg(String background_url,String user_face_url,String user_name){
        try {

            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("static/font/FeiBo.otf"));


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

            // 绘制用户信息栏
            RoundRectangle2D roundRect = new RoundRectangle2D.Double(30, 60, 300, 100, 8, 8);
            g2d.setColor(Color.WHITE); // 设置颜色为白色
            g2d.fill(roundRect); // 填充圆角矩形

            roundRect = new RoundRectangle2D.Double(1090,backgroundImage.getHeight() - 30 , 180, 20, 8, 8);
            g2d.setColor(Color.WHITE); // 设置颜色为白色
            g2d.fill(roundRect); // 填充圆角矩形

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            g2d.drawImage(faceImage,50,45,null);

            // 可以选择设置字体的大小等属性
            Font finalFont = customFont.deriveFont(40f); // 字体大小设置为40

            // 设置文本的颜色
            g2d.setColor(Color.BLACK); // 或者你想要的任何颜色
            g2d.setFont(finalFont);

            // 绘制文本
            g2d.drawString(user_name, 175, 105);

            g2d.drawString(DateUtils.getGreeting(), 175, 145);

            writeCopyright(backgroundImage, "Creat By BBQBot v1.0.3");

            writeDate(backgroundImage,DateUtils.format(new Date(),"yyyy-MM-dd hh:mm:ss"));

            // 释放Graphics2D资源
            g2d.dispose();

            return backgroundImage;

        } catch (IOException | FontFormatException e) {
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

        // 设置文本的颜色
        g2d.setColor(Color.BLACK); // 或者你想要的任何颜色

        Font finalFont = new Font("Arial", Font.ITALIC, 16); // 字体名，样式，大小
        g2d.setFont(finalFont);

        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = finalFont.getStringBounds(copyright, frc);

        // 计算水平居中的x坐标
        int x = (int) (original_image.getWidth() - bounds.getWidth()) / 2;

        // 注意：LineMetrics提供了对字体行高的更精确控制
        LineMetrics metrics = finalFont.getLineMetrics(copyright, frc);
        float ascent = metrics.getAscent(); // 文本基线以上的部分
        float descent = metrics.getDescent(); // 文本基线以下的部分
        float leading = metrics.getLeading(); // 行间距
        float totalHeight = ascent + descent + leading;
        int y = original_image.getHeight() - (int) (totalHeight) + 5;

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
        LineMetrics metrics = finalFont.getLineMetrics(date, frc);
        float ascent = metrics.getAscent(); // 文本基线以上的部分
        float descent = metrics.getDescent(); // 文本基线以下的部分
        float leading = metrics.getLeading(); // 行间距
        float totalHeight = ascent + descent + leading;
        int y = original_image.getHeight() - (int) (totalHeight) + 5;

        g2d.drawString(date, x, y);
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

    /**
     * 判断图片是否正常
     * @param image_path 本地路径
     * @return 布尔值
     */
    public static boolean isNull(String image_path) {
        try {
            // 使用ImageIO读取图片文件
            File file = new File(image_path);
            BufferedImage image = ImageIO.read(file);

            return image == null;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
