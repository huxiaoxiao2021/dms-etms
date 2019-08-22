package com.jd.bluedragon.utils;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 序列号规则判断
 * Created by wangtingwei on 2014/11/3.
 *
 * @see "http://jira.360buy.com/confluence/pages/viewpage.action?pageId=5486438"
 */
public class SerialRuleUtil {

    public static final String SPLIT_CHAR_STRING = "-";

    /**
     * 库房号正则
     */
    private static final Pattern RULE_STORE_CODE_REGEX = Pattern.compile("^([A-z]{1,})-(\\d+)-(\\d+)$");

    /**
     * 判断是否是有效的运单号  Q单也认识为运单 用时请考虑好是不是要支持Q单
     * Add by shipeilin on 2017/08/07
     *
     * @param wayBillCode
     * @return boolean
     */
    public static final boolean isMatchCommonWaybillCode(String wayBillCode) {
        return WaybillUtil.isWaybillCode(wayBillCode) || WaybillUtil.isPickupCode(wayBillCode);
    }

    /**
     * 通过批次号获取目的站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static Integer getReceiveSiteCodeFromSendCode(String sendCode) {
        return BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);
    }

    /**
     * 通过批次号获取始发站点
     *
     * @param sendCode 发货批次号
     * @return
     */
    public static Integer getCreateSiteCodeFromSendCode(String sendCode) {
        return BusinessUtil.getCreateSiteCodeFromSendCode(sendCode);
    }

    /**
     * 判断是否是运单号或者包裹号
     */
    public static boolean isWaybillOrPackageNo(String input) {
        return WaybillUtil.isWaybillCode(input) || WaybillUtil.isPackageCode(input);
    }


    /**
     * 获取运单号
     * 当输入为包裹号时，返回对应的运单号，否则返回null
     * 如果不想返回null 请用WaybillUtil.getWaybillCode
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
     * 生成批次号
     *
     * @param createSiteCode  始发站点
     * @param receiveSiteCode 接收站点
     * @return
     */
    public static String generateSendCode(long createSiteCode, long receiveSiteCode, Date time) {
        if (null == time) {
            time = new Date();
        }
        String timeString = DateHelper.formatDate(time, DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS);
        timeString = timeString.substring(0, timeString.length() - 1);
        long magic = Long.valueOf(String.valueOf(createSiteCode).substring(0, 1) + String.valueOf(receiveSiteCode).substring(0, 1) + timeString) % 7;
        StringBuilder sendCode = new StringBuilder();
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
     *
     * @param storeCode wms-6-1
     * @return
     */
    public static final Integer getStoreIdFromStoreCode(String storeCode) {
        if (storeCode == null) {
            return null;
        }
        Matcher matcher = RULE_STORE_CODE_REGEX.matcher(storeCode.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(3));
        }
        return null;
    }

}
