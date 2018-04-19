package com.jd.bluedragon.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

public class BusinessHelper {

	private final static Logger logger = Logger.getLogger(BusinessHelper.class);

	public static final String PACKAGE_SEPARATOR = "-";
	public static final String PACKAGE_IDENTIFIER_SUM = "S";
	public static final String PACKAGE_IDENTIFIER_NUMBER = "N";
	public static final String PACKAGE_IDENTIFIER_PICKUP = "W";
	public static final String PACKAGE_WAIDAN = "V";

	public static final String AO_BATCH_CODE_PREFIX="Y";
	public static final String PACKAGE_IDENTIFIER_REPAIR = "VY";
	public static final String SOURCE_CODE_ECLP = "ECLP";
	public static final String BUSI_ORDER_CODE_PRE_ECLP = "ESL";
	public static final String BUSI_ORDER_CODE_QWD = "QWD";
	public static final String SOURCE_CODE_CLPS = "CLPS";
	public static final String BUSI_ORDER_CODE_PRE_CLPS = "CSL";
	/**
	 * hash格式分页存储时，分页大小
	 */
	public static final int HASH_KEY_PAGESIZE = 200;
	static{
		init();
	}
	public static String getWaybillCodeByPackageBarcode(String s) {
		if (!BusinessHelper.isPackageCode(s)) {
			return null;
		}
		try {
			if (s.indexOf(BusinessHelper.PACKAGE_SEPARATOR) != -1) {
				return s.split("-")[0];
			} else if (s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER) != -1
					&& s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_SUM) != -1) {
				return s.substring(0, s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER));
			}

		} catch (Exception e) {
			BusinessHelper.logger.error("提取运单号码发生错误， 错误信息为：" + e.getMessage(), e);
		}

		return null;
	}

	private static void init() {
	}

	/**
	 * 根据包裹获得总包裹数
	 * 
	 * @param packageBarcode
	 * @return
	 */
    public static int getPackageNum(String packageBarcode){
    	int sum = 1;
    	if(packageBarcode.indexOf("S")>0 && packageBarcode.indexOf("H")>0){
			sum = Integer.valueOf(packageBarcode.substring(packageBarcode.indexOf("S")+1, packageBarcode.indexOf("H")));
		}else if(packageBarcode.indexOf("-")>0 && (packageBarcode.split("-").length==3||packageBarcode.split("-").length==4)){
			sum = Integer.valueOf(packageBarcode.split("-")[2]);
		}
    	if(sum>BusinessHelper.getMaxNum()){
    		sum =BusinessHelper.getMaxNum();
    	}
    	return sum;
    }

	/**
	 * 从包裹号码提取运单号码.
	 *
	 * @param s
	 *            包裹号码
	 * @return
	 */
	public static String getWaybillCode(String s) {
		if (!BusinessHelper.isPackageCode(s)) {
			return s;
		}

		try {
			if (s.indexOf(BusinessHelper.PACKAGE_SEPARATOR) != -1) {
				return s.substring(0, s.indexOf(BusinessHelper.PACKAGE_SEPARATOR));
			} else if (s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER) != -1) {
				return s.substring(0, s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER));
			}
		} catch (Exception e) {
			BusinessHelper.logger.error("提取运单号码发生错误， 错误信息为：" + e.getMessage(), e);
		}

		return null;
	}

    /**
     * 判断输入字符串是否为包裹号码. 包裹号规则： 123456789N1S1
     *
     * @param s
     *            用来判断的字符串
     * @return 如果此字符串为包裹号，则返回 true，否则返回 false
     */
    public static Boolean isPackageCode(String s) {
        if (StringHelper.isEmpty(s)) {
            return Boolean.FALSE;
        }

        /**
         * 亚一批次号会当箱号使用，排除亚一箱号
         */
        if(s.startsWith(Box.BOX_TYPE_WEARHOUSE)){
            return Boolean.FALSE;
        }

        if (s.indexOf(BusinessHelper.PACKAGE_SEPARATOR) != -1) {
            return Boolean.TRUE;
        } else if (s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER) != -1
                && s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_SUM) != -1) {
            return Boolean.TRUE;
        } 
//        else if (BusinessHelper.isPickupCode(s)) {
//            return Boolean.TRUE;
//        }

        return Boolean.FALSE;
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
		return SerialRuleUtil.isMatchBoxCode(s)||s.toUpperCase().startsWith(BusinessHelper.AO_BATCH_CODE_PREFIX);
	}

	/**
	 * 判断输入字符串是否为运单号码. 包裹号规则： 123456789
	 *
	 * @param s
	 *            用来判断的字符串
	 * @return 如果此字符串为包裹号，则返回 true，否则返回 false
	 */
	public static Boolean isWaybillCode(String s) {
		if (StringHelper.isEmpty(s)) {
			return Boolean.FALSE;
		}

		if (BusinessHelper.isPackageCode(s)) {
			return Boolean.FALSE;
		}
		
		if (BusinessHelper.isBoxcode(s)) {
			return Boolean.FALSE;
		}

		if (s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER) == -1
				&& s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_SUM) == -1
				&& s.indexOf(BusinessHelper.PACKAGE_SEPARATOR) == -1
				&& s.indexOf(Box.BOX_TYPE_FORWARD) == -1
				&& s.indexOf(Box.BOX_TYPE_REVERSE_AFTER_SERVICE) == -1
				&& s.indexOf(Box.BOX_TYPE_REVERSE_LUXURY) == -1
//				&& s.indexOf(Box.BOX_TYPE_REVERSE_REJECTION) == -1 //与箱号T冲突，逆向运单号也有T的情况
				&& s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_PICKUP) == -1) {
			return Boolean.TRUE;
		}
		
		if (s.indexOf(BusinessHelper.PACKAGE_WAIDAN) == 0
				&& s.indexOf(BusinessHelper.PACKAGE_SEPARATOR) == -1) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
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
		if (Constants.BUSSINESS_TYPE_REVERSE == type && BusinessHelper.isReverseSpareCode(aPackageCode)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
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
	 * 判断输入字符串是否为面单号. 包裹号规则： W1234567890
	 *
	 * @param s
	 *            用来判断的字符串
	 * @return 如果此字符串为包裹号，则返回 true，否则返回 false
	 */
	public static Boolean isPickupCode(String s) {
		if (StringHelper.isEmpty(s)) {
			return Boolean.FALSE;
		}

		if (s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_PICKUP) == 0
				&& s.startsWith(BusinessHelper.PACKAGE_IDENTIFIER_PICKUP) ) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
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

        if (PACKAGE_IDENTIFIER_PICKUP.equals(s.substring(1,2))) {
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

		if (PACKAGE_IDENTIFIER_REPAIR.equals(s.substring(0, 2))) {
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

		if (SOURCE_CODE_ECLP.equals(sourceCode)) {
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

		if (busiOrderCode.startsWith(BUSI_ORDER_CODE_PRE_ECLP)) {
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

		if (busiOrderCode.startsWith(BUSI_ORDER_CODE_PRE_CLPS)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * 判断是否是CLPS订单
	 * CLPS : 云仓
	 * @param busiOrderCode  运单中的sourceCode字段 是CLPS
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
	 * @param
	 * @return 开头的单子 返回true
	 */
	public  static Boolean isQWD(String waybillCode){
		if (StringHelper.isEmpty(waybillCode)) {
			return Boolean.FALSE;
		}
		if(waybillCode.indexOf(BUSI_ORDER_CODE_QWD)==0 && waybillCode.startsWith(BusinessHelper.BUSI_ORDER_CODE_QWD)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
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
	 * @return
	 * 	 参数为空，返回 false;
	 *  (0,maxNum] 此范围内的返回 true，其他false
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
	 * @return
	 * 	 参数为空，返回 true;
	 *  (0,maxNum] 不在此范围内的返回 true，其他false
	 */
	public static Boolean checkIntNumNotInRange(Integer intNum) {
		return !BusinessHelper.checkIntNumRange(intNum);
	}
	/**
	 * 判断字符串位置是否标记为1
	 * @param signStr
	 * @param position 标识位
	 * @return
	 */
	public static boolean isSignY(String signStr,int position){
		return isSignChar(signStr,position,Constants.FLG_CHAR_YN_Y);
	}
	/**
	 * 判断字符串位置是否标记为指定的字符
	 * @param signStr
	 * @param position
	 * @param signChar
	 * @return
	 */
	public static boolean isSignChar(String signStr,int position,char signChar){
		if(StringHelper.isNotEmpty(signStr) && signStr.length() >= position){
			return signStr.charAt(position-1)==signChar;
		}
		return false;
	}
	/**
	 * 判断字符串指定的位置是否在指定的字符范围之内
	 * @param signStr 目标字符串
	 * @param position 标识位置
	 * @param chars 字符范围
	 * @return
	 */
	public static boolean isSignInChars(String signStr,int position,char... chars){
		if(StringHelper.isNotEmpty(signStr) 
				&& signStr.length() >= position
				&& chars != null
				&& chars.length > 0){
			char positionChar = signStr.charAt(position-1);
			if(chars.length == 1){
				return chars[0] == positionChar;
			}else{
				for(char tmp:chars){
					if(positionChar == tmp){
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 根据waybillSign和sendSign判断是否城配运单
	 * @param waybillSign 36为1
	 * @param sendPay 146为1
	 * @return
	 */
	public static boolean isUrban(String waybillSign,String sendPay){
		return isSignY(sendPay,146) || isSignY(waybillSign,36);
	}
	
	/**
	 * 1号店订单判断逻辑：sendpay  60-62位 ，034、035、036、037、038、039为一号店订单
	 * @param sendPay 60=0 61=3 62=4 5 6 7 8 9
	 * @return
	 */
	public static boolean isYHD(String sendPay){
		if(isSignChar(sendPay, 60, '0') && isSignChar(sendPay, 61, '3')){
			if(isSignChar(sendPay, 62, '4')||isSignChar(sendPay, 62, '5')||isSignChar(sendPay, 62, '6')||
					isSignChar(sendPay, 62, '7')||isSignChar(sendPay, 62, '8')||isSignChar(sendPay, 62, '9')){
				return true;
			}
		}
		
		return false;
	}
	/**
	 * 根据waybillSign第一位判断是否SOP(标识为 2)或纯外单（标识为 3、6、9、K、Y）
	 * @param waybillSign
	 * @return
	 */
	public static boolean isSopOrExternal(String waybillSign){
		return (isSignChar(waybillSign, 1, '2') ||isExternal(waybillSign));
	}
	/**
	 * 根据waybillSign第一位判断是否纯外单（标识为 3、6、9、K、Y）
	 * @param waybillSign
	 * @return
	 */
	public static boolean isExternal(String waybillSign){
		return (isSignChar(waybillSign, 1, '3')
				||isSignChar(waybillSign, 1, '6')
				||isSignChar(waybillSign, 1, '9')
				||isSignChar(waybillSign, 1, 'K')
				||isSignChar(waybillSign, 1, 'Y'));
	}

	/**
	 * 站点类型判断
	 * @param type
	 * @return
	 */
	public static boolean isSiteType(Integer type){
		return Integer.valueOf(4).equals(type);
	}

    /**
     * 判断是否上传了体积或者重量(重量不为0 或者 长宽高都不为0)
     * @param context 请求上下文
     * @return 是否上传体积或重量
     */
    public static boolean hasWeightOrVolume(WaybillPrintRequest waybillPrintRequest){
    	if(waybillPrintRequest.getWeightOperFlow()==null){
    		return false;
    	}
        if(!Constants.DOUBLE_ZERO.equals(waybillPrintRequest.getWeightOperFlow().getWeight()) 
        	||(!Constants.DOUBLE_ZERO.equals(waybillPrintRequest.getWeightOperFlow().getWidth())
        		&&!Constants.DOUBLE_ZERO.equals(waybillPrintRequest.getWeightOperFlow().getLength())
        		&&!Constants.DOUBLE_ZERO.equals(waybillPrintRequest.getWeightOperFlow().getHigh()))){
            return true;
        }
        return false;
    }
    /**
     * 将体积装换为3个长度值
     * @param volumeFormula 体积值 长*宽*高
     * @return 空字符串，返回空
     */
    public static double[] convertVolumeFormula(String volumeFormula) {
    	if(StringHelper.isEmpty(volumeFormula)){
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
     * @param baseStaffSiteOrgDto
     * @return
     */
    public static boolean isThirdSite(BaseStaffSiteOrgDto baseStaffSiteOrgDto){
    	return baseStaffSiteOrgDto!=null
				&&Constants.THIRD_SITE_TYPE.equals(baseStaffSiteOrgDto.getSiteType())
				&&Constants.THIRD_SITE_SUB_TYPE.equals(baseStaffSiteOrgDto.getSubType());
    }
    /**
     * 验证运单数据是否包含-到付运费，WaybillSign40=2或3时，并且WaybillSign25=2时，freight<=0 返回false
     * @param bigWaybillDto
     * @return
     */
    public static boolean hasFreightForB2b(BigWaybillDto bigWaybillDto){
    	if(bigWaybillDto!=null
    			&&bigWaybillDto.getWaybill()!=null
    			&&StringHelper.isNotEmpty(bigWaybillDto.getWaybill().getWaybillSign())){
    		String waybillSign = bigWaybillDto.getWaybill().getWaybillSign();
    		//WaybillSign40=2或3时，并且WaybillSign25=2时（只外单快运纯配、外单快运仓配并且运费到付），需校验
    		if((isSignChar(waybillSign, 40, '2')||isSignChar(waybillSign, 40, '3'))
    				&&isSignChar(waybillSign, 25, '2')){
    			String freightStr = bigWaybillDto.getWaybill().getFreight();
    			if(NumberHelper.isStringNumber(freightStr)){
    				return NumberHelper.getDoubleValue(freightStr).doubleValue() > 0d;
    			}else{
    				return false;
    			}
    		}
    	}
    	return true;
    }
	/**
	 * 根据waybillSign判断是否B网运单（40位标识为 1、2、3）
	 * @param waybillSign
	 * @return
	 */
	public static boolean isB2b(String waybillSign){
		return isSignInChars(waybillSign, 40,'1','2','3','4','5');
	}
	/**
	 * 获取包裹号的hash存储key,Key:运单号-页码	keyFiled:包裹号，页码=（包裹序号-1)/200+1
	 * @param packageCode 支持72945262907N4S5H30和72945262907-4-5-
	 * @return
	 */
	public static String[] getHashKeysByPackageCode(String packageCode) {
		if(StringHelper.isEmpty(packageCode)){
			return null;
		}
		try {
			int packageIndex = 0;
			String waybillCode = null;
			if (packageCode.indexOf(PACKAGE_SEPARATOR) != -1) {
				String[] strs = packageCode.split(PACKAGE_SEPARATOR);
				if(strs.length>=3){
					waybillCode = strs[0];
					packageIndex = Integer.parseInt(strs[1]);
				}
			} else if (packageCode.indexOf(PACKAGE_IDENTIFIER_NUMBER) != -1
					&& packageCode.indexOf(PACKAGE_IDENTIFIER_SUM) != -1) {
				waybillCode = packageCode.substring(0, packageCode.indexOf(PACKAGE_IDENTIFIER_NUMBER));
				packageIndex = Integer.parseInt(packageCode.substring(
						packageCode.indexOf(PACKAGE_IDENTIFIER_NUMBER)+1, 
						packageCode.indexOf(PACKAGE_IDENTIFIER_SUM)));
			}
			if(packageIndex > 0){
				String key = waybillCode + PACKAGE_SEPARATOR + (packageIndex>HASH_KEY_PAGESIZE?(packageIndex-1)/HASH_KEY_PAGESIZE+1:1);
				return new String[]{key,packageCode};
			}
		} catch (Exception e) {
			logger.error(packageCode+"获取hashKey发生错误， 错误信息为：" + e.getMessage(), e);
		}
		return null;
	}
	/**
	 * 获取hashKey
	 * @param key 键
	 * @param pageIndex 页码
	 * @return key+"-"+ pageIndex
	 */
	public static String getHashKey(String key,int pageIndex) {
		if(StringHelper.isNotEmpty(key)){
			return key + PACKAGE_SEPARATOR + pageIndex;
		}
		return null;
	}
}
