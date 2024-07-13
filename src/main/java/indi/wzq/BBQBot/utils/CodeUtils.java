package indi.wzq.BBQBot.utils;


import indi.wzq.BBQBot.enums.Codes;


public class CodeUtils {

    /***
     * 匹配指令
     * @param str 待匹配的文本
     * @return Codes
     */
    public static Codes matchInstructions(String str) {
        for (Codes value : Codes.values()) {
            if (MatcherUtils.matcherIgnoreCase(str, value.getStr())) {
                return value;
            }
        }
        return null;
    }

}
