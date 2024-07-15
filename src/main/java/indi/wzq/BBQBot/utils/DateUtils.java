package indi.wzq.BBQBot.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 格式化日期 以 format 格式
     * @param date 日期
     * @param format 格式化的格式
     * @return 格式化之后的字符串日期
     */
    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 判断两时间是否是同一天
     * @param date1 时间1
     * @param date2 时间2
     * @return 布尔值
     */
    public static boolean isSameDay(Date date1,Date date2){
        return  format(date1,parsePatterns[0]).equals(format(date2,parsePatterns[0]));
    }

    /**
     * 判断时间2是否是时间1的前一天
     * @param date1 时间1
     * @param date2 时间2
     * @return 布尔值
     */
    public static boolean isYesterday(Date date1, Date date2) {
        // 将java.util.Date转换为java.time.LocalDate
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 判断date1是否是date2的前一天
        return localDate1.equals(localDate2.minusDays(1));
    }

    /**
     * 根据当前时间获得问候语
     * @return 问候语
     */
    public static String getGreeting(){
        LocalTime now = LocalTime.now(); // 获取当前时间
        int hour = now.getHour(); // 获取当前小时

        // 根据小时数判断时间段
        if          (  4 <= hour & hour <  8 ) {
            return "早上好！";
        } else if   (  8 <= hour & hour < 11 ) {
            return "上午好！";
        } else if   ( 11 <= hour & hour < 15 ) {
            return "中午好！";
        } else if   ( 15 <= hour & hour < 19 ) {
            return "下午好！";
        } else {
            return "晚上好！";
        }
    }
}
