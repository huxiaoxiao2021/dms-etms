package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.regex.Pattern;

public class BusinessHelper {

    private final static Logger logger = Logger.getLogger(BusinessHelper.class);
    public static final String PACKAGE_SEPARATOR = "-";
    public static final String PACKAGE_IDENTIFIER_SUM = "S";
    public static final String PACKAGE_IDENTIFIER_NUMBER = "N";

    public static final String TERMINAL_BATCH_CODE_REG = "^R\\d{19}$";//站点批次号正则
    /**
     * hash格式分页存储时，分页大小
     */
    public static final int HASH_KEY_PAGESIZE = 200;

    static {
        init();
    }


    private static void init() {
    }


    /**
     * Y开头的也认为是箱号（上海亚一用）
     */
    public static Boolean isBoxcode(String s) {
        if (StringHelper.isEmpty(s)) {
            return false;
        }
        return BusinessUtil.isBoxcode(s) || s.toUpperCase().startsWith(DmsConstants.AO_BATCH_CODE_PREFIX);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getOwnSign() {
        String ownSignValue = null;
        try {
            ownSignValue = PropertiesHelper.newInstance().getValue(
                    Constants.DEFAULT_OWN_SIGN_KEY);
        } catch (NumberFormatException nfe) {
            BusinessHelper.logger.error("格式化发生异常！", nfe);
        }

        if (ownSignValue == null) {
            ownSignValue = Constants.DEFAULT_OWN_SIGN_VALUE;
        }

        return ownSignValue;
    }

    /**
     * @return 获取最大正整数，默认2000
     */
    public static int getMaxNum() {
        int maxPackNum = 0;
        try {
            maxPackNum = Integer.parseInt(PropertiesHelper.newInstance().getValue(Constants.MAX_PACK_NUM));
        } catch (NumberFormatException nfe) {
            BusinessHelper.logger.error("格式化发生异常！", nfe);
        }
        return maxPackNum <= 0 ? 5000 : maxPackNum;
    }

    /**
     * 验证是否为规定范围内的正整数
     *
     * @param intNum
     * @return 参数为空，返回 false;
     * (0,maxNum] 此范围内的返回 true，其他false
     */
    public static Boolean checkIntNumRange(Integer intNum) {
        if (intNum == null) {
            return Boolean.FALSE;
        }

        if (intNum > 0 && intNum <= BusinessHelper.getMaxNum()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 验证不在为规定范围内的正整数
     *
     * @param intNum
     * @return 参数为空，返回 true;
     * (0,maxNum] 不在此范围内的返回 true，其他false
     */
    public static Boolean checkIntNumNotInRange(Integer intNum) {
        return !BusinessHelper.checkIntNumRange(intNum);
    }


    /**
     * 站点类型判断
     *
     * @param type
     * @return
     */
    public static boolean isSiteType(Integer type) {
        return Integer.valueOf(4).equals(type);
    }

    /**
     * 判断是否上传了体积或者重量(重量不为0 或者 长宽高都不为0)
     *
     * @param waybillPrintRequest 打印请求参数
     * @return 是否上传体积或重量
     */
    public static boolean hasWeightOrVolume(WaybillPrintRequest waybillPrintRequest) {
        if (waybillPrintRequest.getWeightOperFlow() == null) {
            return false;
        }
        if (NumberHelper.gt0(waybillPrintRequest.getWeightOperFlow().getWeight())
                || (NumberHelper.gt0(waybillPrintRequest.getWeightOperFlow().getWidth())
                && NumberHelper.gt0(waybillPrintRequest.getWeightOperFlow().getLength())
                && NumberHelper.gt0(waybillPrintRequest.getWeightOperFlow().getHigh()))) {
            return true;
        }
        return false;
    }

    /**
     * 将体积装换为3个长度值
     *
     * @param volumeFormula 体积值 长*宽*高
     * @return 空字符串，返回空
     */
    public static double[] convertVolumeFormula(String volumeFormula) {
        if (StringHelper.isEmpty(volumeFormula)) {
            return null;
        }
        double[] volumeArray = new double[3];
        String[] volumes = volumeFormula.split("\\*");
        if (volumes.length == 3) {
            volumeArray = ArraysUtil.getOrderArray(volumes);
        }
        return volumeArray;
    }

    /**
     * 验证是否三方站点，siteType=16并且subType=16
     *
     * @param baseStaffSiteOrgDto
     * @return
     */
    public static boolean isThirdSite(BaseStaffSiteOrgDto baseStaffSiteOrgDto) {
        return baseStaffSiteOrgDto != null
                && Constants.THIRD_SITE_TYPE.equals(baseStaffSiteOrgDto.getSiteType())
                && Constants.THIRD_SITE_SUB_TYPE.equals(baseStaffSiteOrgDto.getSubType());
    }

    /**
     * 验证运单数据是否包含-到付运费，WaybillSign40=2或3时，并且WaybillSign25=2时，freight<=0 返回false
     *
     * @param bigWaybillDto
     * @return
     */
    public static boolean hasFreightForB2b(BigWaybillDto bigWaybillDto) {
        if (bigWaybillDto != null
                && bigWaybillDto.getWaybill() != null
                && StringHelper.isNotEmpty(bigWaybillDto.getWaybill().getWaybillSign())) {
            String waybillSign = bigWaybillDto.getWaybill().getWaybillSign();
            //WaybillSign40=2或3时，并且WaybillSign25=2时（只外单快运纯配、外单快运仓配并且运费到付），需校验
            if ((BusinessUtil.isSignChar(waybillSign, 40, '2') || BusinessUtil.isSignChar(waybillSign, 40, '3'))
                    && BusinessUtil.isSignChar(waybillSign, 25, '2')
                    && !reverseB2bNoInterceptFreight(waybillSign)) {
                return NumberHelper.gt0(bigWaybillDto.getWaybill().getFreight());
            }
        }
        return true;
    }

    /**
     * 逆向不计费运单
     * @param waybillSign
     * @return true 不计费，false 计费
     */
    private static boolean reverseB2bNoInterceptFreight(String waybillSign){
        //waybillSign第14位等于D或E，为逆向不计费运单，不用校验运费，不用拦截;(D:原单作废，E:原单拒收因京东原因产生的逆向)
        return BusinessUtil.isSignInChars(waybillSign,14,'D','E');
    }

    /**
     * 验证运单数据是否包含-寄付运费，WaybillSign62=1，且WaybillSign25=3时，freight<=0 返回false
     *
     * @param bigWaybillDto
     * @return
     */
    public static boolean hasSendFreightForB2b(BigWaybillDto bigWaybillDto) {
        if (bigWaybillDto != null
                && bigWaybillDto.getWaybill() != null
                && StringHelper.isNotEmpty(bigWaybillDto.getWaybill().getWaybillSign())) {
            String waybillSign = bigWaybillDto.getWaybill().getWaybillSign();
            //WaybillSign62=1时，并且WaybillSign25=3时（只外单快运纯配、外单快运仓配并且运费寄付），需校验
            if (BusinessUtil.isSignChar(waybillSign, 62, '1')
                    && BusinessUtil.isSignChar(waybillSign, 25, '3')
                    && !reverseB2bNoInterceptFreight(waybillSign)) {
                return NumberHelper.gt0(bigWaybillDto.getWaybill().getFreight());
            }
        }
        return true;
    }

    /**
     * 到付运费或COD  TopayTotalReceivable > 0
     *
     * @param bigWaybillDto
     * @return
     */
    public static boolean isCODOrFreightCollect(BigWaybillDto bigWaybillDto) {
        if (bigWaybillDto != null && bigWaybillDto.getWaybill() != null && bigWaybillDto.getWaybill().getTopayTotalReceivable() != null) {
            if (bigWaybillDto.getWaybill().getTopayTotalReceivable().compareTo(new Double(0)) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 运单为城配类型，sendpay 第146位等于1，表示为自营城配 || waybill_sign 第36位等于1，表示为外单城配
     * @param waybillSign
     * @param sendPay
     * @return
     */
    public static boolean isDmsToVendor(String waybillSign,String sendPay) {
        boolean waybillSignFlag = false;
        boolean sendPayFlag = false;
        //waybill_sign 第36位等于1，表示为外单城配,waybillSignFlag置为true
        if(StringHelper.isNotEmpty(waybillSign) && BusinessUtil.isSignChar(waybillSign, 36, '1')){
            waybillSignFlag = true;
        }
        //sendPay 第146位等于1，表示为自营城配
        if(StringHelper.isNotEmpty(sendPay) && BusinessUtil.isSignChar(sendPay, 146, '1')){
            sendPayFlag = true;
        }
        return waybillSignFlag || sendPayFlag;
    }
    /**
     * 校验运单总体积和总重量重泡比
     * 重泡比超过正常范围168:1到330:1
     *
     * @param weight
     * @param volume
     * @return
     */
    public static boolean checkWaybillWeightAndVolume(Double weight, Double volume) {
        if (weight == null || volume == null || weight.compareTo(0.0) < 0 || volume.compareTo(0.0) < 0) {
            return false;
        }
        if ((weight / volume < Constants.CBM_DIV_KG_MIN_LIMIT) || (weight / volume > Constants.CBM_DIV_KG_MAX_LIMIT)) {
            return false;
        }
        return true;
    }

    /**
     * 获取包裹号的hash存储key,Key:运单号-页码	keyFiled:包裹号，页码=（包裹序号-1)/200+1
     *
     * @param packageCode 支持72945262907N4S5H30和72945262907-4-5-
     * @return
     */
    public static String[] getHashKeysByPackageCode(String packageCode) {
        if (StringHelper.isEmpty(packageCode)) {
            return null;
        }
        try {
            int packageIndex = 0;
            String waybillCode = null;
            if (packageCode.indexOf(PACKAGE_SEPARATOR) != -1) {
                String[] strs = packageCode.split(PACKAGE_SEPARATOR);
                if (strs.length >= 3) {
                    waybillCode = strs[0];
                    packageIndex = Integer.parseInt(strs[1]);
                }
            } else if (packageCode.indexOf(PACKAGE_IDENTIFIER_NUMBER) != -1
                    && packageCode.indexOf(PACKAGE_IDENTIFIER_SUM) != -1) {
                waybillCode = packageCode.substring(0, packageCode.indexOf(PACKAGE_IDENTIFIER_NUMBER));
                packageIndex = Integer.parseInt(packageCode.substring(
                        packageCode.indexOf(PACKAGE_IDENTIFIER_NUMBER) + 1,
                        packageCode.indexOf(PACKAGE_IDENTIFIER_SUM)));
            }
            if (packageIndex > 0) {
                String key = waybillCode + PACKAGE_SEPARATOR + (packageIndex > HASH_KEY_PAGESIZE ? (packageIndex - 1) / HASH_KEY_PAGESIZE + 1 : 1);
                return new String[]{key, packageCode};
            }
        } catch (Exception e) {
            logger.error(packageCode + "获取hashKey发生错误， 错误信息为：" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取hashKey
     *
     * @param key       键
     * @param pageIndex 页码
     * @return key+"-"+ pageIndex
     */
    public static String getHashKey(String key, int pageIndex) {
        if (StringHelper.isNotEmpty(key)) {
            return key + PACKAGE_SEPARATOR + pageIndex;
        }
        return null;
    }

    /**
     * 通过运单标识 判断是否需求包装耗材
     * <p>
     * 72 位 是1  标识需要
     *
     * @param waybillSign
     * @return
     */
    public static boolean isNeedConsumable(String waybillSign) {
        return BusinessUtil.isSignChar(waybillSign, 72, '1');
    }

    /**
     * 根据运单号生成第一个包裹号
     *
     * @param waybillCode
     * @return
     */
    //fixme 考虑滑道号生成
    public static String getFirstPackageCodeByWaybillCode(String waybillCode) {
        if (!BusinessUtil.isBoxcode(waybillCode) && !WaybillUtil.isPackageCode(waybillCode)) {
            if (WaybillUtil.isLasWaybillCode(waybillCode)) {
                return waybillCode + "-1-1";
            } else if (WaybillUtil.isWaybillCode(waybillCode)) {
                return waybillCode + "-1-1-";
            }
        }
        return waybillCode;
    }

    public static boolean isSendCode(String sendCode) {
        return BusinessUtil.isSendCode(sendCode);
    }

    /**
     * 判断是否是终端的发货批次号
     * @param batchCode 终端发货批次号 R1130842623972495360
     * @return
     */
    public static boolean isTerminalBatchCode(String batchCode) {
        if (StringHelper.isEmpty(batchCode)) {
            return Boolean.FALSE;
        }
        return batchCode.matches(TERMINAL_BATCH_CODE_REG);
    }

    /**
     * 判断是否满足同城当日达的条件
     * waybill_sign
     * 第55位等于0 （表示非生鲜专送）
     * 且第16位等于1 （表示当日达）
     * 且第31位等于2 （表示同城配送）
     * 且第63位等于1 （中心站网络）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isSameCityOneDay(String waybillSign) {
        return BusinessUtil.isSignChar(waybillSign, 55, '0') &&
                BusinessUtil.isSignChar(waybillSign, 16, '1') &&
                BusinessUtil.isSignChar(waybillSign, 31, '2') &&
                BusinessUtil.isSignChar(waybillSign, 63, '1');
    }


    /**
     * 判断是否是 需要显示代配站点新通路订单  sendPay 148-149 = 39且146=1
     *
     * @param sendPay
     * @return
     */
    public static boolean isNewPathWay(String sendPay) {
        if (BusinessUtil.isSignChar(sendPay, 148, '3') &&
                BusinessUtil.isSignChar(sendPay, 149, '9') &&
                BusinessUtil.isSignChar(sendPay, 146, '1')) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * traderSign的第29位：【1. 京配超区不转3PL】
     * 不允许转三方配送
     *
     * @param traderSign
     * @return
     */
    public static boolean canThreePLSchedule(String traderSign) {
        if (StringUtils.isNotEmpty(traderSign) && (BusinessUtil.isSignChar(traderSign, 29, '1'))) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 根据waybillSign判断是否签单返还运单
     * @param waybillSign
     * @return
     */
    public static boolean isSignatureReturnWaybill(String waybillSign){
        return BusinessUtil.isSignInChars(waybillSign,4,'1','2','3','4','9');
    }

    /**
     * 划分理赔状态
     *
     * @param type
     * @return
     */
    public static String getLPStatus(int type) {
        switch (type) {
            case 10:
            case 20:
            case 22:
            case 30:
            case 32:
            case 50:
            case 53:
                //理赔中
                //10-已保存,
                //20-赔付审核中,,
                //22-赔付审核通过,
                //30-待商家确认,,
                //32-商家已确认,,
                //50-赔付中（已推送结算）,
                //53-结算失败
                return LocalClaimInfoRespDTO.LP_STATUS_DOING;
            case 51:
                //理赔完成
                //51-已完成（结算成功）
                return LocalClaimInfoRespDTO.LP_STATUS_DONE;
        }
        //无理赔状态
        //21-赔付审核不通过
        //31-商家拒绝
        //40-已撤回,
        //41-已取消
        //90-已关闭
        return LocalClaimInfoRespDTO.LP_STATUS_NONE;

    }

    /**
     * c2c 且为到付或寄付
     * waybill_sign第29位为8
     * 且 waybill_sign第25位为2或3
     */
    public static boolean isC2cDFJF(String waybillSign) {
        if (StringUtils.isBlank(waybillSign)){
            return false;
        }
        if (isC2c(waybillSign) && (BusinessUtil.isSignChar(waybillSign, 25, '2') || BusinessUtil.isSignChar(waybillSign, 25, '3'))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * c2c
     * waybill_sign第29位为8
     */
    public static boolean isC2c(String waybillSign) {
        if (StringUtils.isBlank(waybillSign)){
            return false;
        }
        if (BusinessUtil.isSignChar(waybillSign, 29, '8')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * c2c 且为到付或寄付，且为正向
     * waybill_sign第61位为0标记正向
     */
    public static boolean isC2cForward(String waybillSign) {
        if (StringUtils.isBlank(waybillSign)){
            return false;
        }
        return BusinessUtil.isSignChar(waybillSign, 61, '0') && isC2cDFJF(waybillSign);
    }

    /**
     * 判断是否是B网分拣
     * @param siteType
     * @return
     */
    public static boolean isBSite(Integer siteType){
        if(siteType==null){
            return false;
        }
        return siteType.equals(new Integer(6420));
    }

    /**
     * 是否为逆向回仓
     *
     * @param waybillSign
     * @return
     */
    public static boolean isReverseToStore(String waybillSign) {
        if (StringUtils.isBlank(waybillSign)) {
            return false;
        }
        if (BusinessUtil.isSignChar(waybillSign, 88, '1')) {
            return true;
        } else {
            return false;
        }
    }

    public static final String WMS_SITE_TYPE = PropertiesHelper.newInstance().getValue("wms_type");

    /**
     * 判断站点类型是否为仓
     *
     * @param siteType
     * @return
     */
    public static boolean isWms(Integer siteType) {
        if (siteType == null) {
            return false;
        }
        return siteType.equals(new Integer(WMS_SITE_TYPE));
    }
    /**
     * 判断eclp能否打印,40位为1或者40位为2并且80位为1和2
     * @param waybillSign
     * @return
     */
    public static boolean isEclpCanPrint(String waybillSign) {
        if (StringUtils.isBlank(waybillSign)) {
            return false;
        }
        return BusinessUtil.isSignChar(waybillSign, 40, '1')
        		||(BusinessUtil.isSignChar(waybillSign, 40, '2')
        				&& BusinessUtil.isSignInChars(waybillSign, 80, '1' ,'2'));
    }

    /**
     * 是否是已旧换新订单
     * @param sendPay
     * @return
     */
    public static boolean isYJHX(String sendPay){
        if (StringUtils.isBlank(sendPay)) {
            return false;
        }
        return !BusinessUtil.isSignChar(sendPay, 275, '0');
    }
}
