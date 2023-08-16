package com.jd.bluedragon.utils;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/11 18:43
 * @Description: ASCP 客服交互常量类
 */
public class ASCPContants {


    public static final int SOURCE_SYSTEM_CODE = 40;//来源系统code 需要分配 传40

    public static final int DEAL_TYPE = 1;//处理方式 外呼

    public static final int BUSINESS_ID = 408;//业务类型标识

    public static final int CODE_TYPE = 50;//业务类型标识


    //违禁品业务类型标识
    public static final int CONTRABAND_BUSINESS_ID =413;
    //异常一级原因编码
    public static final String CONTRABAND_EXPT_ONE_LEVEL ="4130100";
    //异常一级原因名称
    public static final String CONTRABAND_EXPT_ONE_LEVEL_NAME ="配送异常报备";
    //异常二级原因编码
    public static final String CONTRABAND_EXPT_TWO_LEVEL ="4130102";
    //异常二级原因名称
    public static final String CONTRABAND_EXPT_TWO_LEVEL_NAME ="违禁品退回";

    public static final int WAYBILL_TYPE_SELF =1;

    public static final int WAYBILL_TYPE_OTHER =5;


}
