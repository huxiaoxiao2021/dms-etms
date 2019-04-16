package com.jd.bluedragon.dms.utils;

import com.jd.etms.waybill.util.UniformValidateUtil;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author tangchunqing
 * @Description: 运单相关操作工具类
 * @date 2018年10月12日 16时:06分
 */
public class WaybillUtil {
    private final static Logger logger = Logger.getLogger(WaybillUtil.class);

    public static boolean isWaybillCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isWaybillCode(waybillCode);
    }

    public static boolean isPackageCode(String packageCode) {
        return WaybillCodeRuleValidateUtil.isPackageCode(packageCode);
    }

    public static boolean isEffectiveOperateCode(String operateCode) {
        return WaybillCodeRuleValidateUtil.isEffectiveOperateCode(operateCode);
    }

    public static boolean isBusiWaybillCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isBusiWaybillCode(waybillCode);
    }

    public static boolean isJDWaybillCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isJDWaybillCode(waybillCode);
    }

    public static boolean isSurfaceCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isSurfaceCode(waybillCode);
    }

    public static boolean isReturnCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isReturnCode(waybillCode);
    }

    public static boolean isSwitchCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isSwitchCode(waybillCode);
    }

    public static boolean isMobileWareHouseReturnCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isMobileWareHouseReturnCode(waybillCode);
    }

    public static boolean isPickupCode(String pickupCode) {
        return WaybillCodeRuleValidateUtil.isPickupCode(pickupCode);
    }

    public static boolean isLasWaybillCode(String waybillCode) {
        return WaybillCodeRuleValidateUtil.isLasWaybillCode(waybillCode);
    }

    public static String getWaybillCodeByPackCode(String packCode) {
        return WaybillCodeRuleValidateUtil.getWaybillCodeByPackCode(packCode);
    }

    public static boolean isFirstPack(String packCode) {
        return WaybillCodeRuleValidateUtil.isFirstPack(packCode);
    }

    public static int getPackNumByPackCode(String packCode) {
        return WaybillCodeRuleValidateUtil.getPackNumByPackCode(packCode);
    }

    public static int getPackIndexByPackCode(String packCode) {
        return WaybillCodeRuleValidateUtil.getPackIndexByPackCode(packCode);
    }

    /**
     * 根据包裹号解析运单号
     *
     * @param packCode
     * @return
     */
    public static String getWaybillCode(String packCode) {
        if (WaybillCodeRuleValidateUtil.isWaybillCode(packCode)) {
            return packCode;
        }
        String waybillCode = WaybillCodeRuleValidateUtil.getWaybillCodeByPackCode(packCode);
        if (StringUtils.isBlank(waybillCode)) {
            return packCode;
        }
        return waybillCode;
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
        if (type == null || StringUtils.isEmpty(aPackageCode)) {
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
        if (StringUtils.isEmpty(s)) {
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
        return WaybillCodeRuleValidateUtil.isMobileWareHouseReturnCode(s);
    }

    /**
     * 判断是否是维修外单
     * MCS : 维修外单缩写,备件库定义的
     *
     * @param s
     * @return
     */
    public static Boolean isMCSCode(String s) {
        if (StringUtils.isEmpty(s)) {
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
        if (StringUtils.isEmpty(sourceCode)) {
            return Boolean.FALSE;
        }

        if (DmsConstants.SOURCE_CODE_ECLP.equals(sourceCode)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 判断是否是仓配ECLP订单
     * ECLP : 仓储开发平台
     *
     * @param busiOrderCode 运单中的busiOrderCode字段,判断它是不是esl开头单号
     * @return
     */
    public static Boolean isECLPByBusiOrderCode(String busiOrderCode) {
        if (StringUtils.isEmpty(busiOrderCode)) {
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
        logger.info("生成所有包裹号：" + packcode);
        List<String> list = new ArrayList<String>();
        //如果是有效的包裹号，根据包裹总数生成包裹号列表
        if (WaybillCodeRuleValidateUtil.isPackageCode(packcode)) {
            int totalPackageNum = WaybillCodeRuleValidateUtil.getPackNumByPackCode(packcode);//包裹总数
            if (totalPackageNum >= 100) {
                logger.warn("生成包裹大于100：" + totalPackageNum + "packcode:" + packcode);
            }
            //超过2W 认为是不正常的单子
            if (totalPackageNum > DmsConstants.MAX_NUMBER) {
                logger.error("生成包裹出错，包裹总数过大：" + totalPackageNum + "packcode:" + packcode);
                return list;
            }
            //上海亚一包裹号处理
            if (packcode.contains("N") && packcode.contains("S") && packcode.contains("H")) {
                for (int i = 1; i <= totalPackageNum; i++) {
                    String packageCode = packcode.replaceFirst("N\\d+S", "N" + i + "S");
                    list.add(packageCode);
                }
            } else if (packcode.contains("-")) {
                //非亚一包裹号处理
                for (int i = 1; i <= totalPackageNum; i++) {
                    String packageCode = packcode.replaceFirst("-\\d+-", "-" + i + "-");
                    list.add(packageCode);
                }
            }
            return list;
        }
        return list;
    }

    /**
     * 获取包裹序列号
     */
    public static String getPackageIndex(String packageCode) {
        if (WaybillUtil.isPackageCode(packageCode)) {
            int currentPackageNum = WaybillUtil.getCurrentPackageNum(packageCode);
            int totalPackageNum = WaybillUtil.getPackNumByPackCode(packageCode);
            return currentPackageNum + "/" + totalPackageNum;
        }
        return "0/0";
    }

    /**
     * 截取包裹号后缀
     * @param packageCode
     * @return
     */
    public static String getPackageSuffix(String packageCode){
        int index = -1;
        if (WaybillUtil.isPackageCode(packageCode)) {
            if (packageCode.indexOf("N") > 0 && packageCode.indexOf("S") > 0) {
                index = packageCode.indexOf("N");
            } else if (packageCode.indexOf("-") > 0 && (packageCode.split("-").length == 3 || packageCode.split("-").length == 4)) {
                index = packageCode.indexOf("-");
            }
        }
        if(index < 0){
            return null;
        } else{
            return packageCode.substring(index);
        }

    }

    /**
     * 根据包裹号得到 滑道号
     * 85179219739-1-1-91 返回91，85358175547N1S1H2 返回2。
     * @param packageCode 包裹号
     * @return 没有滑道号返回 null
     */
    public static String getCrossCodeOnPackageCode(String packageCode){
        Matcher matcher = DmsConstants.PACKAGE_CODE_CROSSCODE_REGEX.matcher(packageCode);
        if(matcher.matches()){
            return matcher.group(6);
        }
        return null;
    }


}
