package com.jd.bluedragon.dms.utils;

import org.apache.commons.lang.StringUtils;

public class WaybillVasUtil {

    //增值服务默认10位 只允许有 0 1
    public static final String DEFAULT_VAS_SIGN = "0000000000";

    /*
     * 包裹有话说增值服务编码
     * */
    public static final String PACKAGE_SAY = "festivalAttachment";

    public static final char ON_CHAR = '1';

    public static final char OFF_CHAR = '0';

    // 包裹优化说
    public static final int PACK_SAY_SIGN_INDEX = 1;

    // 精准送仓
    public static final int JZSC_SIGN_INDEX = 2;
    // 精准送仓值
    public static final String JZSC_VALUE = "ll-a-0060";

    /**
     *  打标 包裹有话说 第一位 1
     * @param sign
     * @return
     */
    public static final String markingPackageSaySign(String sign){
        return markingSign(PACK_SAY_SIGN_INDEX,sign);
    }

    /**
     *   是否 包裹有话说 第一位 1
     * @param sign
     * @return
     */
    public static final boolean isPackageSay(String sign){
        return BusinessUtil.isSignChar(sign,PACK_SAY_SIGN_INDEX,ON_CHAR);
    }

    /**
     *  打标 精准送仓 第二位 1
     * @param sign
     * @return
     */
    public static final String markingJZSCSign(String sign){
        return markingSign(JZSC_SIGN_INDEX,sign);
    }

    /**
     *   是否 精准送仓 第二位 1
     * @param sign
     * @return
     */
    public static final boolean isJZSC(String sign){
        return BusinessUtil.isSignChar(sign,JZSC_SIGN_INDEX,ON_CHAR);
    }


    private static final String markingSign(int index,String sign){
        if(StringUtils.isBlank(sign) || sign.length() != DEFAULT_VAS_SIGN.length()){
            //重置默认打标信息
            sign = DEFAULT_VAS_SIGN;
        }
        StringBuilder nSign = new StringBuilder(sign);
        nSign.setCharAt(index-1,ON_CHAR);
        return nSign.toString();
    }
}
