package indi.wzq.BBQBot.utils;

import indi.wzq.BBQBot.plugin.group.GroupCodes;

import java.io.*;

public class FileUtils {

    public static String saveImageFile(byte[] bytes ,String file_path , String file_name){
        String filePath = file_path + file_name;

        // 创建 File 对象
        File file = new File(filePath);

        // 确保文件夹存在
        if (!file.getParentFile().exists()) {
            if (file.getParentFile().mkdirs()) return null;
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

    public static byte[] readImageFile(String class_path){
        try {
            InputStream resourceAsStream = GroupCodes.class.getResourceAsStream(class_path);

            if (resourceAsStream == null) {
                throw new IOException("文件未找到: " + class_path);
            }

            // 使用ByteArrayOutputStream来存储图片数据
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096]; // 4KB缓冲区

            int bytesRead;

            // 读取InputStream到ByteArrayOutputStream
            while ((bytesRead = resourceAsStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            // 将ByteArrayOutputStream转换为byte数组
            byte[] bytes = baos.toByteArray();

            // 关闭流
            resourceAsStream.close();

            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
