package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.log4j.Logger;

import java.util.regex.Pattern;

public class BusinessHelper {

    private final static Logger logger = Logger.getLogger(BusinessHelper.class);

    public static final String PACKAGE_SEPARATOR = "-";
    public static final String PACKAGE_IDENTIFIER_SUM = "S";
    public static final String PACKAGE_IDENTIFIER_NUMBER = "N";
    /**
     * hash格式分页存储时，分页大小
     */
    public static final int HASH_KEY_PAGESIZE = 200;

    static {
        init();
    }

    /**
     * 根据包裹号解析运单号
     *
     * @param packCode
     * @return
     */
    @Deprecated
    public static String getWaybillCodeByPackageBarcode(String packCode) {
        return WaybillUtil.getWaybillCode(packCode);
    }

    private static void init() {
    }

    /**
     * 根据包裹获得总包裹数
     *
     * @param packageBarcode
     * @return
     */
    @Deprecated
    public static int getPackageNum(String packageBarcode) {
        return WaybillUtil.getPackNumByPackCode(packageBarcode);
    }

    /**
     * 根据包裹获得当前所属包裹数
     *
     * @param packageBarcode
     * @return
     */
    @Deprecated
    public static int getCurrentPackageNum(String packageBarcode) {
        return WaybillUtil.getCurrentPackageNum(packageBarcode);
    }

    /**
     * 从包裹号码提取运单号码.
     *
     * @param s 包裹号码
     * @return
     */
    @Deprecated
    public static String getWaybillCode(String s) {
        return WaybillUtil.getWaybillCode(s);
    }

    /**
     * 判断输入字符串是否为包裹号码. 包裹号规则： 123456789N1S1
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    @Deprecated
    public static Boolean isPackageCode(String s) {
        return WaybillUtil.isPackageCode(s);
    }

    /**
     * 判断输入字符串是否为箱号. 箱号规则： 箱号： B(T,G) C(S) 010F001 010F002 12345678 。
     * B，正向；T，逆向；G取件退货;C普通物品；S奢侈品；2-8位，出发地编号；9-15位，到达地编号；最后8位，流水号。一共23位。 前面有两个字母
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为箱号，则返回 true，否则返回 false
     */
    @Deprecated
    public static Boolean isBoxcode(String s) {
        return BusinessUtil.isBoxcode(s);
    }

    /**
     * 建议使用 WaybillUtil.isWaybillCode(s);
     * 判断输入字符串是否为运单号码. 包裹号规则： 123456789
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    @Deprecated
    public static Boolean isWaybillCode(String s) {
        return WaybillUtil.isWaybillCode(s);
    }


    /**
     * 验证是否为备件退货
     * 合法返回 true, 不合法返回 false
     *
     * @param type
     * @param aPackageCode
     * @return
     */
    @Deprecated
    public static Boolean isReverseSpare(Integer type, String aPackageCode) {
        return WaybillUtil.isReverseSpare(type, aPackageCode);
    }

    /**
     * 验证是否为备件条码
     * 合法返回 true, 不合法返回 false
     *
     * @param s
     * @return
     */
    @Deprecated
    public static Boolean isReverseSpareCode(String s) {
        return WaybillUtil.isReverseSpareCode(s);
    }

    /**
     * 判断输入字符串是否为面单号. 包裹号规则： W1234567890
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    @Deprecated
    public static Boolean isPickupCode(String s) {
        return WaybillUtil.isSurfaceCode(s);
    }

    /**
     * 这种类型的  WW123456789 包裹号返回true
     *
     * @param s 用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    @Deprecated
    public static Boolean isPickupCodeWW(String s) {
        return WaybillUtil.isPickupCodeWW(s);
    }

    /**
     * 判断是否是维修外单
     * MCS : 维修外单缩写,备件库定义的
     *
     * @param s
     * @return
     */
    @Deprecated
    public static Boolean isMCSCode(String s) {
        return WaybillUtil.isMCSCode(s);
    }

    /**
     * 判断是否是ECLP订单
     * ECLP : 仓储开发平台
     *
     * @param sourceCode 运单中的sourceCode字段,判断它是不是ECLP开头单号
     * @return
     */
    @Deprecated
    public static Boolean isECLPCode(String sourceCode) {
        return WaybillUtil.isECLPCode(sourceCode);
    }

    /**
     * 判断是否是ECLP订单
     * ECLP : 仓储开发平台
     *
     * @param busiOrderCode 运单中的busiOrderCode字段,判断它是不是esl开头单号
     * @return
     */
    @Deprecated
    public static Boolean isECLPByBusiOrderCode(String busiOrderCode) {
        return WaybillUtil.isECLPByBusiOrderCode(busiOrderCode);
    }


    /**
     * 判断是否是CLPS订单
     * CLPS : 云仓
     *
     * @param busiOrderCode 运单中的BusiOrderCode字段,判断它是不是CSL开头单号
     * @return
     */
    @Deprecated
    public static Boolean isCLPSByBusiOrderCode(String busiOrderCode) {
        return BusinessUtil.isCLPSByBusiOrderCode(busiOrderCode);
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
     * 判断字符串位置是否标记为1
     *
     * @param signStr
     * @param position 标识位
     * @return
     */
    public static boolean isSignY(String signStr, int position) {
        return BusinessUtil.isSignY(signStr,position);
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
        return BusinessUtil.isSignChar(signStr,position,signChar);
    }

    /**
     * 判断字符串指定的位置是否在指定的字符范围之内
     *
     * @param signStr  目标字符串
     * @param position 标识位置
     * @param chars    字符范围
     * @return
     */
    public static boolean isSignInChars(String signStr, int position, char... chars) {
      return BusinessUtil.isSignInChars(signStr,position,chars);
    }

    /**
     * 根据waybillSign和sendSign判断是否城配运单
     *
     * @param waybillSign 36为1
     * @param sendPay     146为1
     * @return
     */
    public static boolean isUrban(String waybillSign, String sendPay) {
        return BusinessUtil.isUrban(waybillSign,sendPay);
    }

    /**
     * 1号店订单判断逻辑：sendpay  60-62位 ，034、035、036、037、038、039为一号店订单
     *
     * @param sendPay 60=0 61=3 62=4 5 6 7 8 9
     * @return
     */
    public static boolean isYHD(String sendPay) {
        return BusinessUtil.isYHD(sendPay);
    }

    /**
     * 根据waybillSign第一位判断是否SOP(标识为 2)或纯外单（标识为 3、6、9、K、Y）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isSopOrExternal(String waybillSign) {
        return BusinessUtil.isSopOrExternal(waybillSign);
    }

    /**
     * 根据waybillSign第一位判断是否纯外单（标识为 3、6、9、K、Y）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isExternal(String waybillSign) {
        return BusinessUtil.isExternal(waybillSign);
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
            if ((isSignChar(waybillSign, 40, '2') || isSignChar(waybillSign, 40, '3'))
                    && isSignChar(waybillSign, 25, '2')) {
                return NumberHelper.gt0(bigWaybillDto.getWaybill().getFreight());
            }
        }
        return true;
    }

    /**
     * 根据waybillSign判断是否B网运单（40位标识为 1、2、3）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isB2b(String waybillSign) {
        return BusinessUtil.isB2b(waybillSign);
    }

    /**
     * 根据waybillSign判断是否病单（34位标识为 2）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isSick(String waybillSign) {
        return BusinessUtil.isSick(waybillSign);
    }

    /**
     * 根据waybillSign判断是否加履中心订单 （29 位 9 ）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isPerformanceOrder(String waybillSign) {
        return BusinessUtil.isPerformanceOrder(waybillSign);
    }

    /**
     * 包裹半收 标识 waybillSign 27位 （0-不半收 1-全收半退 2-包裹半收 3-运单明细半收 4-包裹明细半收）
     *
     * @param waybillSign
     * @return
     */
    public static boolean isPackageHalf(String waybillSign) {
        return BusinessUtil.isPackageHalf(waybillSign);
    }

    /**
     * 支持协商再投
     *
     * @param waybillSign
     * @return
     */
    public static boolean isConsultationTo(String waybillSign) {
        return BusinessUtil.isConsultationTo(waybillSign);
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
     * 通过运单标识 判断是否需求称重
     * <p>
     * 66 位 是1  标识不称重
     *
     * @param waybillSign
     * @return
     */
    public static boolean isNoNeedWeight(String waybillSign) {
        return BusinessUtil.isNoNeedWeight(waybillSign);
    }

    /**
     * 通过运单标识 判断B网耗材
     * <p>
     * 72位：是否需要包装服务： 0---不需要 默认，1---需要包装服务
     * 25 位 是3  标识 B网耗材不允许修改，只能操作确认
     *
     * @param waybillSign
     * @return
     */
    public static boolean isWaybillConsumableOnlyConfirm(String waybillSign) {
        return BusinessUtil.isWaybillConsumableOnlyConfirm(waybillSign);
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
     * 获取始发道口号类型
     * <p>自营：sendpay137位为1，则为航运订单标识，航填,其他为普通
     * <p>外单：waybillsign第31位等于1，则为航空，waybillsign第31位等于0，且waybillsign第67位等于1则为航填
     *
     * @param waybillSign
     * @param sendPay
     * @return
     */
    public static Integer getOriginalCrossType(String waybillSign, String sendPay) {
       return  BusinessUtil.getOriginalCrossType(waybillSign,sendPay);
    }

    /**
     * 判断是否招商银行业务运单，waybill_sign第54位等于3时
     *
     * @param waybillSign
     * @return
     */
    public static boolean isCMBC(String waybillSign) {
        return BusinessUtil.isCMBC(waybillSign);
    }

    /**
     * 是否是RMA标识的运单
     *
     * @param waybillSign
     * @return
     */
    public static boolean isRMA(String waybillSign) {
        return BusinessUtil.isRMA(waybillSign);
    }

	/**
	 * 通过运单标识 判断是否需求包装耗材
	 *
	 *  72 位 是1  标识需要
	 * @param waybillSign
	 * @return
	 */
	public static boolean isNeedConsumable(String waybillSign){
		return isSignChar(waybillSign, 72, '1');
	}

	/**
	 * 根据运单号生成第一个包裹号
	 * @param waybillCode
	 * @return
	 */
	//fixme 考虑滑道号生成
	public static String getFirstPackageCodeByWaybillCode(String waybillCode){
        if(!BusinessUtil.isBoxcode(waybillCode) && !WaybillUtil.isPackageCode(waybillCode)){
            if(WaybillUtil.isLasWaybillCode(waybillCode)){
                return waybillCode + "-1-1";
            }else if (WaybillUtil.isWaybillCode(waybillCode)){
                return waybillCode + "-1-1-";
            }
        }
        return waybillCode;
	}
}
