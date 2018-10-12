package com.jd.bluedragon.dms.utils;

import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

import static com.jd.bluedragon.dms.utils.DmsConstants.BUSI_ORDER_CODE_QWD;
import static com.jd.bluedragon.dms.utils.DmsConstants.SOURCE_CODE_CLPS;

/**
 * @author tangchunqing
 * @Description: 运单相关操作工具类
 * @date 2018年10月12日 16时:06分
 */
public class WaybillUtil extends WaybillCodeRuleValidateUtil {

    /**
     * 箱号正则表达式
     */
    private static final Pattern RULE_BOXCODE_REGEX = Pattern.compile("^[A-Z]{2}[A-Z0-9]{14,16}[0-9]{8}$");

    public static final String AO_BATCH_CODE_PREFIX="Y";
    /**
     * 根据包裹号解析运单号
     *
     * @param packCode
     * @return
     */
    public static String getWaybillCodeByPackCode(String packCode){
        if (isWaybillCode(packCode)){
            return packCode;
        }else{
            return WaybillCodeRuleValidateUtil.getWaybillCodeByPackCode(packCode);
        }
    }
    /**
     * 根据包裹获得当前所属包裹数
     *
     * @param packageBarcode
     * @return
     */
    public static int getCurrentPackageNum(String packageBarcode){
        int num = 1;
        if(packageBarcode.indexOf("N")>0 && packageBarcode.indexOf("S")>0){
            num = Integer.valueOf(packageBarcode.substring(packageBarcode.indexOf("N")+1, packageBarcode.indexOf("S")));
        }else if(packageBarcode.indexOf("-")>0 && (packageBarcode.split("-").length==3||packageBarcode.split("-").length==4)){
            num = Integer.valueOf(packageBarcode.split("-")[1]);
        }
        return num;
    }

    /**
     * 判断输入字符串是否为箱号. 箱号规则： 箱号： B(T,G) C(S) 010F001 010F002 12345678 。
     * B，正向；T，逆向；G取件退货;C普通物品；S奢侈品；2-8位，出发地编号；9-15位，到达地编号；最后8位，流水号。一共23位。 前面有两个字母
     *
     * @param s
     *            用来判断的字符串
     * @return 如果此字符串为箱号，则返回 true，否则返回 false
     */

    public static Boolean isBoxcode(String s) {
        if (StringHelper.isEmpty(s)) {
            return Boolean.FALSE;
        }
        return isMatchBoxCode(s)||s.toUpperCase().startsWith(WaybillUtil.AO_BATCH_CODE_PREFIX);
    }

    /**
     * 判断是否为箱号
     *
     * @param boxCode 待验证字符
     * @return
     */
    public static boolean isMatchBoxCode(String boxCode) {
        return RULE_BOXCODE_REGEX.matcher(boxCode.trim().toUpperCase()).matches();
    }

    /**
     * 验证是否为备件退货
     * 	合法返回 true, 不合法返回 false
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
     * 	合法返回 true, 不合法返回 false
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
     * 验证POP运单号
     * 	合法返回 true, 不合法返回 false
     *
     * @param waybillCode
     * @return
     */
    public static Boolean isPopWaybillCode(String waybillCode) {
        if (StringUtils.isBlank(waybillCode) || waybillCode.length() < 8) {
            return Boolean.FALSE;
        }
        return waybillCode.matches("^[1-9]{1}\\d*$");
    }

    /**
     * 判断输入字符串是否为面单号. 包裹号规则： W1234567890
     *
     * @param s
     *            用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    public static boolean isSurfaceCode(String s) {
       return WaybillCodeRuleValidateUtil.isSurfaceCode(s);
    }

    /**
     *   这种类型的  WW123456789 包裹号返回true
     *
     * @param s
     *            用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    public static Boolean isPickupCodeWW(String s) {
        if (StringHelper.isEmpty(s)) {
            return Boolean.FALSE;
        }
        if (DmsConstants.PACKAGE_IDENTIFIER_PICKUP.equals(s.substring(1,2))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否是维修外单
     * MCS : 维修外单缩写,备件库定义的
     * @param s
     * @return
     */
    public static Boolean isMCSCode(String s) {
        if (StringHelper.isEmpty(s)) {
            return Boolean.FALSE;
        }

        if (DmsConstants.PACKAGE_IDENTIFIER_REPAIR.equals(s.substring(0, 2))) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 判断是否是ECLP订单
     * ECLP : 仓储开发平台
     * @param sourceCode  运单中的sourceCode字段,判断它是不是ECLP开头单号
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
     * @param busiOrderCode  运单中的busiOrderCode字段,判断它是不是esl开头单号
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
     * 判断是否是CLPS订单
     * CLPS : 云仓
     * @param busiOrderCode  运单中的BusiOrderCode字段,判断它是不是CSL开头单号
     * @return
     */
    public static Boolean isCLPSByBusiOrderCode(String busiOrderCode) {
        if (StringHelper.isEmpty(busiOrderCode)) {
            return Boolean.FALSE;
        }

        if (busiOrderCode.startsWith(DmsConstants.BUSI_ORDER_CODE_PRE_CLPS)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 判断是否是CLPS订单
     * CLPS : 云仓
     *
     * @param soucreCode 运单中的sourceCode字段 是CLPS
     * @return
     */
    public static Boolean isCLPSBySoucreCode(String soucreCode) {
        if (StringHelper.isEmpty(soucreCode)) {
            return Boolean.FALSE;
        }

        if (soucreCode.toUpperCase().equals(SOURCE_CODE_CLPS)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * “QWD”开头的单子 返回true
     *
     * @param
     * @return 开头的单子 返回true
     */
    public static Boolean isQWD(String waybillCode) {
        if (StringHelper.isEmpty(waybillCode)) {
            return Boolean.FALSE;
        }
        if (waybillCode.indexOf(BUSI_ORDER_CODE_QWD) == 0 && waybillCode.startsWith(BUSI_ORDER_CODE_QWD)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断字符串位置是否标记为1
     *
     * @param signStr
     * @param position 标识位
     * @return
     */
    public static boolean isSignY(String signStr, int position) {
        return isSignChar(signStr, position, DmsConstants.FLG_CHAR_YN_Y);
    }

    /**
     * 判断字符串位置是否标记为指定的字符
     *
     * @param signStr
     * @param position
     * @param signChar
     * @return
     */
    public static boolean isSignChar(String signStr, int position, char signChar) {
        if (StringHelper.isNotEmpty(signStr) && signStr.length() >= position) {
            return signStr.charAt(position - 1) == signChar;
        }
        return false;
    }
}
