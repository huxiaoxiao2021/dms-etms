package com.jd.bluedragon.utils;

/**
 * @ClassName TagSignHelper
 * @Description
 * @Author wyh
 * @Date 2022/4/10 18:55
 **/
public class TagSignHelper {

    /**
     * 默认位数
     */
    public static final int DEFAULT_DIGIT = 50;

    public static final char DEFAULT_PLACEHOLDER = '0';

    /**
     * 初始化默认长度的占位符
     * @return
     */
    public static String initDefaultPlaceholder() {
        return initSpecificSign(DEFAULT_DIGIT);
    }

    /**
     * 初始化指定长度的占位符
     * @param digit
     * @return
     */
    public static String initSpecificSign(Integer digit) {
        int rightDigit = NumberHelper.isPositiveNumber(digit) ? digit : DEFAULT_DIGIT;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rightDigit; i++) {
            sb.append(DEFAULT_PLACEHOLDER);
        }

        return sb.toString();
    }

    /**
     * 设置指定位置上的占位符
     * @param sign
     * @param position 从1开始
     * @param character
     * @return
     */
    public static String setPositionSign(String sign, int position, Character character) {
        if (position < 1 || position > sign.length()) {
            return sign;
        }
        StringBuilder sb = new StringBuilder(sign);
        sb.replace(position - 1, position, character.toString());

        return sb.toString();
    }
}
