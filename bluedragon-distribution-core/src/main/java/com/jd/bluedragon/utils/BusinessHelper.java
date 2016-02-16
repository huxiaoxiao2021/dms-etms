package com.jd.bluedragon.utils;

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
        } else if (BusinessHelper.isPickupCode(s)) {
            return Boolean.TRUE;
        }

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
		return SerialRuleUtil.isMatchBoxCode(s);
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

		if (s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_NUMBER) == -1
				&& s.indexOf(BusinessHelper.PACKAGE_IDENTIFIER_SUM) == -1
				&& s.indexOf(BusinessHelper.PACKAGE_SEPARATOR) == -1
				&& s.indexOf(Box.BOX_TYPE_FORWARD) == -1
				&& s.indexOf(Box.BOX_TYPE_REVERSE_AFTER_SERVICE) == -1
				&& s.indexOf(Box.BOX_TYPE_REVERSE_LUXURY) == -1
				&& s.indexOf(Box.BOX_TYPE_REVERSE_REJECTION) == -1
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
		return s.matches("^[P]{1}(\\p{Lu}){1}\\d{16}$");
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
		return maxPackNum <= 0 ? 2000 : maxPackNum;
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

}
