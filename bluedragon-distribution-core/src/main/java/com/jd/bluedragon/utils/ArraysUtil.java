package com.jd.bluedragon.utils;

import org.apache.commons.lang.math.NumberUtils;

import java.util.Arrays;

/**
 * 数组工具类
 * <p>
 * <p>
 * Created by lixin39 on 2018/2/1.
 */
public class ArraysUtil {

    /**
     * 按照由小到大进行排序，将字符串数组转换为double类型数组
     *
     * @param array
     * @return
     */
    public static double[] getOrderArray(String[] array) {
        double[] orderArray = new double[array.length];
        for (int i = 0, len = orderArray.length; i < len; i++) {
            if (array[i] != null && NumberUtils.isNumber(array[i].toString())) {
                orderArray[i] = Double.valueOf(array[i].toString());
            } else {
                orderArray[i] = 0.0;
            }
        }
        Arrays.sort(orderArray);
        return orderArray;
    }

}
