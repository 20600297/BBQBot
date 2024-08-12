package indi.wzq.BBQBot.utils;

import com.alibaba.fastjson2.JSON;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileUtils {

    public static String saveImageFile(byte[] bytes ,String file_path , String file_name){
        String filePath = file_path + file_name;

        // 创建 File 对象
        File file = new File(filePath);

        // 确保文件夹存在
        if (!file.getParentFile().exists()) {
            if(!file.getParentFile().mkdirs()) System.out.println("创建 "+ filePath +" 异常");
        }

        // 使用 FileOutputStream 将二进制数据写入文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes); // 将二进制数据写入文件
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过类路径加载包内的img文件
     * @param class_path 类路径
     * @return img字节组
     */
    public static byte[] readImgByClasspath(String class_path){
        try {
            InputStream resourceAsStream = FileUtils.class
                    .getClassLoader()
                    .getResourceAsStream(class_path);

            if (resourceAsStream == null) {
                throw new IOException("资源文件未找到：" + class_path);
            }

            // 使用ByteArrayOutputStream来存储图片数据
            ByteArrayOutputStream base = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096]; // 4KB缓冲区

            int bytesRead;

            // 读取InputStream到ByteArrayOutputStream
            while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
                base.write(buffer, 0, bytesRead);
            }

            // 将ByteArrayOutputStream转换为byte数组
            byte[] bytes = base.toByteArray();

            // 关闭流
            resourceAsStream.close();

            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过类路径加载包内的json文件
     * @param class_path 类路径
     * @return json字符串
     */
    public static String readJsonByClasspath(String class_path){
        try {
            InputStream inputStream = FileUtils.class
                    .getClassLoader()
                    .getResourceAsStream(class_path);

            if (inputStream == null) {
                throw new IOException("资源文件未找到：" + class_path);
            }

            // 使用BufferedReader来读取InputStream中的内容
            StringBuilder jsonStringBuilder = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );

            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line).append('\n');
            }

            return jsonStringBuilder.toString().trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] bufferedImage2Bytes(BufferedImage image){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return  imageBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 cookies 写入json文件
     * @param map cookie的map
     * @param file_path 文件路径
     */
    public static void saveCookieMap(Map<String,String> map , String file_path){
        try (FileWriter writer = new FileWriter(file_path)) {
            String jsonString = JSON.toJSONString(map);
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取 cookies
     * @param file_path 文件路径
     * @return cookie
     */
    public static String readCookie(String file_path){
        try (FileReader reader = new FileReader(file_path)) {
            // 读取整个文件内容为一个字符串
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            String jsonString = sb.toString();

            Map<String, String> map = JSON.parseObject(jsonString, Map.class);

            StringBuilder cookieBuilder = new StringBuilder();
            boolean isFirstEntry = true;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!isFirstEntry) {
                    cookieBuilder.append(";");
                }
                cookieBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                isFirstEntry = false;
            }
            return cookieBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
