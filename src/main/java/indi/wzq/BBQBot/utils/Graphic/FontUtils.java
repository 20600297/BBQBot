package indi.wzq.BBQBot.utils.Graphic;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontUtils {
    public static final String MaoKen = "/static/font/MaoKenYuanZhuTi.ttf";
    public static final String Sakura = "/static/font/sakura.ttf";

    public static Font GetFontByClasspath(String class_path){
        try {

            InputStream inputStream = GraphicUtils.class.getResourceAsStream(class_path);

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

}
