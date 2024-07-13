package indi.wzq.BBQBot.utils.onebot;


import indi.wzq.BBQBot.utils.MatcherUtils;

public class CqMatcher {
    /**
     * 判断是否CQ表情码
     *
     * @param str 字符串
     * @return true/false
     */
    public static boolean isCqFace(String str) {
        return MatcherUtils.matcher(str, "CQ:face,id=[0-9]+");
    }

    /**
     * 判断是否CQ Image
     *
     * @param str 字符串
     * @return true/false
     */
    public static boolean isCqImage(String str) {
        return MatcherUtils.matcher(str, "CQ:image,(.*?)url=(.*?)?term");
    }

    /**
     * 判断是否携带@
     *
     * @param str 字符串
     * @return true/false
     */
    public static boolean isCqAt(String str) {
        return MatcherUtils.matcher(str, "CQ:at,qq=[1-9][0-9]{4,}");
    }


}
