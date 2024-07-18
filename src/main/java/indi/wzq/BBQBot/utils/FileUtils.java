package indi.wzq.BBQBot.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
