package com.jd.bluedragon.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.box.domain.Box;

public class BusinessHelper {

	private final static Logger logger = Logger.getLogger(BusinessHelper.class);

	private static final String PACKAGE_SEPARATOR = "-";
	private static final String PACKAGE_IDENTIFIER_SUM = "S";
	private static final String PACKAGE_IDENTIFIER_NUMBER = "N";
	private static final String PACKAGE_IDENTIFIER_PICKUP = "W";
	private static final String PACKAGE_WAIDAN = "V";

    private static final String AO_BATCH_CODE_PREFIX="Y";
	private static final String PACKAGE_IDENTIFIER_REPAIR = "VY";
	private static final String SOURCE_CODE_ECLP = "ECLP";
	private static final String BUSI_ORDER_CODE_PRE_ECLP = "ESL";
	private static final String BUSI_ORDER_CODE_QWD = "QWD";
	/**
	 * waybillSign打标字符字典，存放打标
	 */
	private static Map<Integer,Map<Character,String>> WAYBILL_SIGN_TEXT_DIC = new HashMap<Integer,Map<Character,String>>();
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
		// TODO Auto-generated method stub
		Map<Character,String> sign4 = new HashMap<Character,String>(2);
		sign4.put('1',"签单返还");
		Map<Character,String> sign10 = new HashMap<Character,String>(16);
		sign10.put('1',"普通");
		sign10.put('2',"常温");
		sign10.put('3',"填仓");
		sign10.put('4',"特配");
		sign10.put('5',"鲜活");
		sign10.put('6',"控温");
		sign10.put('7',"冷藏");
		sign10.put('8',"冷冻");
		sign10.put('9',"深冷");
		Map<Character,String> sign31 = new HashMap<Character,String>(4);
		sign31.put('0',"特惠送");
		sign31.put('1',"特准送");
		WAYBILL_SIGN_TEXT_DIC.put(Constants.WAYBILL_SIGN_POSITION_SIGN_BACK, sign4);
		WAYBILL_SIGN_TEXT_DIC.put(Constants.WAYBILL_SIGN_POSITION_DISTRIBUT_TYPE, sign10);
		WAYBILL_SIGN_TEXT_DIC.put(Constants.WAYBILL_SIGN_POSITION_TRANSPORT_MODE, sign31);
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
	 * 获取waybillSign，标识位对应的描述信息，字典中没有设置，则返回""
	 * @param waybillSign 运单打标字符串
	 * @param signPositions 需要获取的打标位置，从1开始
	 * @return
	 */
	public static Map<Integer,String> getWaybillSignTexts(String waybillSign,Integer... signPositions){
		Map<Integer,String> res = new HashMap<Integer,String>(8);
		if(StringHelper.isNotEmpty(waybillSign)
				&&signPositions!=null){
			char[] waybillSignChars = waybillSign.toCharArray();
			String signText = "";
			for(Integer position:signPositions){
				signText = null;
				if(position>0&&position<=waybillSignChars.length){
					if(WAYBILL_SIGN_TEXT_DIC.containsKey(position)){
						signText = WAYBILL_SIGN_TEXT_DIC.get(position).get(waybillSignChars[position-1]);
					}
				}
				if(signText==null){
					signText = "";
				}
				res.put(position, signText);
			}
		}
		return res;
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
//		sendPay = "00000000100000000000000002001000030000100000000000000000000036000000000000000000000000000000000000000000003400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
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
}
