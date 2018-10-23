package com.jd.bluedragon.dms.utils;

import com.jd.etms.waybill.util.UniformValidateUtil;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 运单相关操作工具类
 * @date 2018年10月12日 16时:06分
 */
public class WaybillUtil extends WaybillCodeRuleValidateUtil {

    /**
     * 根据包裹号解析运单号
     *
     * @param packCode
     * @return
     */
    public static String getWaybillCode(String packCode) {
        if (WaybillCodeRuleValidateUtil.isWaybillCode(packCode) && !WaybillCodeRuleValidateUtil.isPackageCode(packCode)) {
            return packCode;
        }
        return WaybillCodeRuleValidateUtil.getWaybillCodeByPackCode(packCode);
    }

    /**
     * 是否是运单号 京东订单(9到19位数字)、 11位（W开头的面单） 12位数字或V开头运单（B商家运单号）
     * 或F开头的返单号、WW开头的移动仓返单号
     * 兼容新运单号格式（JD开头）
     */
    public static boolean isWaybillCode(String waybillCode) {
        if (WaybillCodeRuleValidateUtil.isWaybillCode(waybillCode) && !WaybillCodeRuleValidateUtil.isPackageCode(waybillCode)) {
            return true;
        }
        return false;
    }

    /**
     * 验证是否为备件退货
     * 合法返回 true, 不合法返回 false
     *
     * @param type
     * @param aPackageCode
     * @return
     */
    public static Boolean isReverseSpare(Integer type, String aPackageCode) {
        if (type == null || StringHelper.isEmpty(aPackageCode)) {
            return Boolean.FALSE;
        }
        if (DmsConstants.BUSSINESS_TYPE_REVERSE == type && isReverseSpareCode(aPackageCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 验证是否为备件条码
     * 合法返回 true, 不合法返回 false
     *
     * @param s
     * @return
     */
    public static Boolean isReverseSpareCode(String s) {
        if (StringHelper.isEmpty(s)) {
            return Boolean.FALSE;
        }
        //正则改为2个字符|null+16位数字（8位日期+8位序列）
        return s.matches("^([A-Za-z]{2}|null)\\d{16}$");
    }

    /**
     * 根据包裹获得当前所属包裹数
     *
     * @param packageBarcode
     * @return
     */
    public static int getCurrentPackageNum(String packageBarcode) {
        int num = 1;
        if (packageBarcode.indexOf("N") > 0 && packageBarcode.indexOf("S") > 0) {
            num = Integer.valueOf(packageBarcode.substring(packageBarcode.indexOf("N") + 1, packageBarcode.indexOf("S")));
        } else if (packageBarcode.indexOf("-") > 0 && (packageBarcode.split("-").length == 3 || packageBarcode.split("-").length == 4)) {
            num = Integer.valueOf(packageBarcode.split("-")[1]);
        }
        return num;
    }

    /**
     * 这种类型的  WW123456789 包裹号返回true
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    public static Boolean isPickupCodeWW(String s) {
        return isMobileWareHouseReturnCode(s);
    }

    /**
     * 判断是否是维修外单
     * MCS : 维修外单缩写,备件库定义的
     *
     * @param s
     * @return
     */
    public static Boolean isMCSCode(String s) {
        if (StringHelper.isEmpty(s)) {
            return Boolean.FALSE;
        }
        if (UniformValidateUtil.isWaybillCode(s)) {
            return s.startsWith("JDY");
        }
        if (DmsConstants.PACKAGE_IDENTIFIER_REPAIR.equals(s.substring(0, 2))) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 判断是否是ECLP订单
     * ECLP : 仓储开发平台
     *
     * @param sourceCode 运单中的sourceCode字段,判断它是不是ECLP开头单号
     * @return
     */
    public static Boolean isECLPCode(String sourceCode) {
        if (StringHelper.isEmpty(sourceCode)) {
            return Boolean.FALSE;
        }

        if (DmsConstants.SOURCE_CODE_ECLP.equals(sourceCode)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 判断是否是ECLP订单
     * ECLP : 仓储开发平台
     *
     * @param busiOrderCode 运单中的busiOrderCode字段,判断它是不是esl开头单号
     * @return
     */
    public static Boolean isECLPByBusiOrderCode(String busiOrderCode) {
        if (StringHelper.isEmpty(busiOrderCode)) {
            return Boolean.FALSE;
        }
        if (busiOrderCode.startsWith(DmsConstants.BUSI_ORDER_CODE_PRE_ECLP)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
    /**
     * 根据包裹号 生成所有的包裹号
     */
    public static List<String> generateAllPackageCodes(String packcode) {
        List<String> list = new ArrayList<String>();
        //如果是有效的包裹号，根据包裹总数生成包裹号列表
        if (WaybillUtil.isPackageCode(packcode)) {
            int totalPackageNum = WaybillUtil.getPackNumByPackCode(packcode);//包裹总数
            //上海亚一包裹号处理
            if (packcode.contains("N") && packcode.contains("S") && packcode.contains("H")) {
                for (int i = 1; i <= totalPackageNum; i++) {
                    String packageCode =  packcode.replaceFirst("N\\d+S","N"+i+"S");
                    list.add(packageCode);
                }
            } else if (packcode.contains("-")) {
                //非亚一包裹号处理
                for (int i = 1; i <= totalPackageNum; i++) {
                    String packageCode = packcode.replaceFirst("-\\d+-","-"+i+"-");
                    list.add(packageCode);
                }
            }
            return list;
        }
        list.add(packcode);
        return list;
    }
}
