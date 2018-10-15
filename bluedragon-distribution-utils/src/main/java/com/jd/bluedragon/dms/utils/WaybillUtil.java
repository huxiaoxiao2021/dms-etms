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
        return WaybillCodeRuleValidateUtil.getWaybillCodeByPackCode(packCode);
    }

//    /**
//     * 验证POP运单号
//     * 合法返回 true, 不合法返回 false
//     *
//     * @param waybillCode
//     * @return
//     */
//    public static Boolean isPopWaybillCode(String waybillCode) {
//        if (StringUtils.isBlank(waybillCode) || waybillCode.length() < 8) {
//            return Boolean.FALSE;
//        }
//        return waybillCode.matches("^[1-9]{1}\\d*$");
//    }

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
//        if (StringHelper.isEmpty(s)) {
//            return Boolean.FALSE;
//        }
//        if (DmsConstants.PACKAGE_IDENTIFIER_PICKUP.equals(s.substring(1, 2))) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
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
        if (UniformValidateUtil.isWaybillCode(busiOrderCode)) {
            return busiOrderCode.startsWith("JDL");
        }
        if (busiOrderCode.startsWith(DmsConstants.BUSI_ORDER_CODE_PRE_ECLP)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

//    /**
//     * 判断是否是CLPS订单
//     * CLPS : 云仓
//     *
//     * @param soucreCode 运单中的sourceCode字段 是CLPS
//     * @return
//     */
//    public static Boolean isCLPSBySoucreCode(String soucreCode) {
//        if (StringHelper.isEmpty(soucreCode)) {
//            return Boolean.FALSE;
//        }
//
//        if (soucreCode.toUpperCase().equals(SOURCE_CODE_CLPS)) {
//            return Boolean.TRUE;
//        }
//
//        return Boolean.FALSE;
//    }

//    /**
//     * “QWD”开头的单子 返回true
//     *
//     * @param
//     * @return 开头的单子 返回true
//     */
//    public static Boolean isQWD(String waybillCode) {
//        if (StringHelper.isEmpty(waybillCode)) {
//            return Boolean.FALSE;
//        }
//        if (UniformValidateUtil.isWaybillCode(waybillCode)) {
//            //需完善
//        }
//        if (waybillCode.indexOf(BUSI_ORDER_CODE_QWD) == 0 && waybillCode.startsWith(BUSI_ORDER_CODE_QWD)) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
//    }

    /**
     * 根据包裹号 生成所有的包裹号
     */
    public static List<String> generateAllPackageCodes(String packcode) {
        List<String> list = new ArrayList<String>();

        //如果是有效的包裹号，根据包裹总数生成包裹号列表
        if (WaybillUtil.isPackageCode(packcode)) {
            String waybillCode = WaybillUtil.getWaybillCode(packcode);//运单号
            int totalPackageNum = WaybillUtil.getPackNumByPackCode(packcode);//包裹总数
            String portCode = "";//道口号

            //非大件的包裹号有道口号，大件的包裹号没有道口号
            if (!WaybillUtil.isLasWaybillCode(waybillCode)) {
                int portCodeIndex = -1;

                //定位最后一个-或H，获取道口号
                if (packcode.lastIndexOf("-") > -1) {
                    portCodeIndex = packcode.lastIndexOf("-");
                } else {
                    portCodeIndex = packcode.lastIndexOf("H");
                }

                if (portCodeIndex != -1) {
                    portCode = packcode.substring(portCodeIndex + 1);
                }
            }

            //上海亚一包裹号处理
            if (packcode.contains("N") && packcode.contains("S") && packcode.contains("H")) {
                for (int i = 1; i <= totalPackageNum; i++) {
                    String packageCode = waybillCode + "N" + i + "S" + totalPackageNum + "H" + portCode;
                    list.add(packageCode);
                }
            } else if (packcode.contains("-")) {
                //非亚一包裹号处理
                for (int i = 1; i <= totalPackageNum; i++) {
                    String packageCode = waybillCode + "-" + i + "-" + totalPackageNum;

                    //大件包裹号没有道口号，只有前两个-
                    if (!WaybillUtil.isLasWaybillCode(waybillCode)) {
                        packageCode = packageCode + "-" + portCode;
                    }
                    list.add(packageCode);
                }
            }
            return list;
        }
        list.add(packcode);
        return list;
    }
}
