package com.jd.bluedragon.dms.utils;

import org.apache.commons.lang.StringUtils;

public class WaybillVasUtil {

    //增值服务默认10位 只允许有 0 1
    public static final String DEFAULT_VAS_SIGN = "0000000000";

    public static final char ON_CHAR = '1';

    public static final char OFF_CHAR = '0';

    /**
     *  打标 包裹有话说 第一位 1
     * @param sign
     * @return
     */
    public static final String markingPackageSaySign(String sign){
        return markingSign(WaybillVasEnum.PACKAGE_SAY.getIndex(), sign);
    }

    /**
     *   是否 包裹有话说 第一位 1
     * @param sign
     * @return
     */
    public static final boolean isPackageSay(String sign){
        return BusinessUtil.isSignChar(sign,WaybillVasEnum.PACKAGE_SAY.getIndex(),ON_CHAR);
    }

    /**
     *  打标 精准送仓 第二位 1
     * @param sign
     * @return
     */
    public static final String markingJZSCSign(String sign){
        return markingSign(WaybillVasEnum.JZSC_VALUE.getIndex(), sign);
    }

    /**
     *   是否 精准送仓 第二位 1
     * @param sign
     * @return
     */
    public static final boolean isJZSC(String sign){
        return BusinessUtil.isSignChar(sign,WaybillVasEnum.JZSC_VALUE.getIndex(),ON_CHAR);
    }

    /**
     * 定温送 打标 第3位 1
     * @param sign
     * @return
     */
    public static final String markingDWSSign(String sign){
        return markingSign(WaybillVasEnum.FIX_TEMPERATURE_RANGE.getIndex(), sign);
    }
    /**
     * 是否 定温送 第3位 1
     * @param sign
     * @return
     */
    public static final boolean isDWS(String sign){
        return BusinessUtil.isSignChar(sign,WaybillVasEnum.FIX_TEMPERATURE_RANGE.getIndex(),ON_CHAR);
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

    public enum WaybillVasEnum {

        PACKAGE_SAY("festivalAttachment","包裹有话说",1),
        JZSC_VALUE("ll-a-0060","精准送仓",2),
        FIX_TEMPERATURE_RANGE("ll-a-0079","定温送",3),
        ;

        /**
         * 增值服务编码
         */
        private String vasCode;
        /**
         * 增值服务名称
         */
        private String vasName;
        /**
         * 索引项
         */
        private int index;

        WaybillVasEnum(String vasCode, String vasName, int index){
            this.vasCode = vasCode;
            this.vasName = vasName;
            this.index = index;
        }

        public String getVasCode() {
            return vasCode;
        }

        public String getVasName() {
            return vasName;
        }

        public int getIndex() {
            return index;
        }
    }
}
