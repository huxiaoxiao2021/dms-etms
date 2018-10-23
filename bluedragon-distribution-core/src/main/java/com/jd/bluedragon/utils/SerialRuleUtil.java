package com.jd.bluedragon.utils;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 序列号规则判断
 * Created by wangtingwei on 2014/11/3.
 * @see "http://jira.360buy.com/confluence/pages/viewpage.action?pageId=5486438"
 */
public class SerialRuleUtil {

    public static final String SPLIT_CHAR_STRING = "-";

    /**
     * 逆向箱号正则表达式:
     * TC:退货普通
     * TS:退货奢侈品
     * TW:逆向内配
     */
    private static final Pattern RULE_REVERSE_BOXCODE_REGEX = Pattern.compile("^(TC|TS|TW){1}[A-Z0-9]{14,16}[0-9]{8}$");


    /**
     * 提取发货批次号中站点正则
     */
    private static final Pattern RULE_SEND_CODE_SITE_CODE_REGEX = Pattern.compile("^[Y|y]?(\\d+)-(\\d+)-([0-9]{14,})$");

    /**
     * 库房号正则
     */
    private static final Pattern RULE_STORE_CODE_REGEX = Pattern.compile("^([A-z]{1,})-(\\d+)-(\\d+)$");

    /**
     * 板号正则表达式
     */
    private static final Pattern RULE_BOARD_CODE_REGEX = Pattern.compile("^B[0-9]{14}$");

    /**
     * 判断是否是有效的运单号
     * Add by shipeilin on 2017/08/07
     * @param wayBillCode
     * @return boolean
     */
    @Deprecated
    public static final boolean isMatchCommonWaybillCode(String wayBillCode){
        return WaybillUtil.isWaybillCode(wayBillCode);
    }
    /**
     * 判断是否是有效的包裹号
     * @param packageCode
     * @return boolean
     */
    @Deprecated
    public static final boolean isMatchCommonPackageCode(String packageCode){
        return WaybillUtil.isPackageCode(packageCode);
    }

    /**
     * 通过批次号获取目的站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static final Integer getReceiveSiteCodeFromSendCode(String sendCode) {
        Matcher matcher = RULE_SEND_CODE_SITE_CODE_REGEX.matcher(sendCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(2));
        }
        return null;
    }

    /**
     * 通过批次号获取始发站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static final Integer getCreateSiteCodeFromSendCode(String sendCode) {
        if (null == sendCode) {
            return null;
        }
        Matcher matcher = RULE_SEND_CODE_SITE_CODE_REGEX.matcher(sendCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    @Deprecated
    public static final boolean isMatchAllWaybillCode(String input) {
        return WaybillUtil.isWaybillCode(input);
    }

    /**
     * 是否匹配京东自营运单号规则
     * 运单号为订单号 目前9位数字。
     *
     * @param input 待验证字符串
     * @return
     */
    @Deprecated
    public static boolean isMatchWaybillNo(String input) {
        return WaybillUtil.isJDWaybillCode(input);
    }


    /**
     * 是否匹配接货运单号
     */
    @Deprecated
    public static boolean isMatchReceiveWaybillNo(String input) {
        return WaybillUtil.isBusiWaybillCode(input);
    }

    /**
     * 是否匹配包裹号包括自营及接货
     */
    @Deprecated
    public static boolean isMatchAllPackageNo(String input) {
        return WaybillUtil.isPackageCode(input);
    }


    /**
     * 是否匹配接货运单号和分拣运单号
     */
    @Deprecated
    public static boolean isMatchAllWaybillNo(String input) {
        return WaybillUtil.isWaybillCode(input);
    }

    /**
     * 判断是否是运单号或者包裹号
     */
    @Deprecated
    public static boolean isWaybillOrPackageNo(String input) {
        return WaybillUtil.isWaybillCode(input) || WaybillUtil.isPackageCode(input);
    }

    /**
     * 生产包裹号码
     */
    @Deprecated
    public static List<String> generateAllPackageCodes(String input) {
        return WaybillUtil.generateAllPackageCodes(input);
    }

    /**
     * 获取运单号
     * 当输入为包裹号时，返回对应的运单号，否则返回自身
     *
     * @param input 包裹号或运单号
     * @return
     */
    @Deprecated
    public static String getWaybillCode(String input) {
        if (null == input) {
            return input;
        }
        return WaybillUtil.getWaybillCode(input);
    }

    /**
     * 验证输入是否为正整数
     *
     * @param input 输入字符串
     */
    public static boolean isMatchNumeric(String input) {
        Pattern reg = Pattern.compile("^[1-9]{1}[0-9]*$");
        return reg.matcher(input.trim()).matches();
    }

    /**
     * 判断是否为箱号
     *
     * @param boxCode 待验证字符
     * @return
     */
    public static boolean isMatchBoxCode(String boxCode) {
        return BusinessUtil.isBoxcode(boxCode);
    }

    /**
     * 生成批次号
     *
     * @param createSiteCode  始发站点
     * @param receiveSiteCode 接收站点
     * @return
     */
    public static String generateSendCode(long createSiteCode, long receiveSiteCode, Date time) {
        if(null==time)
        {
            time=new Date();
        }
        String timeString = DateHelper.formatDate(time,DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS);
        timeString= timeString.substring(0,timeString.length()-1);
        long magic=Long.valueOf(String.valueOf(createSiteCode).substring(0,1)+String.valueOf(receiveSiteCode).substring(0,1)+timeString)%7;
        StringBuilder sendCode=new StringBuilder();
        sendCode.append(String.valueOf(createSiteCode));
        sendCode.append(SPLIT_CHAR_STRING);
        sendCode.append(String.valueOf(receiveSiteCode));
        sendCode.append(SPLIT_CHAR_STRING);
        sendCode.append(timeString);
        sendCode.append(String.valueOf(magic));
        return sendCode.toString();
    }

    /**
     * 根据库房编码获取库房号
     * @param storeCode wms-6-1
     * @return
     */
    public static final Integer getStoreIdFromStoreCode(String storeCode) {
    	if(storeCode==null){
    		return null;
    	}
        Matcher matcher = RULE_STORE_CODE_REGEX.matcher(storeCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(3));
        }
        return null;
    }

    /**
     * 判断是否板号
     * @param boardCode
     * @return
     */
    public static final boolean isBoardCode(String boardCode){
        if (StringUtils.isNotBlank(boardCode) && RULE_BOARD_CODE_REGEX.matcher(boardCode.trim().toUpperCase()).matches()) {
            return true;
        }
        return false;
    }



    /**
     * 判断是否逆向箱号（TC\TS\TW)
     * TC:退货普通
     * TS:退货奢侈品
     * TW:逆向内配
     * @param boxCode
     * @return
     */
    public static final boolean isReverseBoxCode(String boxCode){
        if (StringUtils.isNotBlank(boxCode) && RULE_REVERSE_BOXCODE_REGEX.matcher(boxCode.trim().toUpperCase()).matches()) {
            return true;
        }
        return false;
    }

}
