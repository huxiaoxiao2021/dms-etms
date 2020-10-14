package com.jd.bluedragon.utils;

import com.jd.dms.logger.external.BusinessLogProfiler;

import java.util.HashMap;
import java.util.Map;

/**
 * 体验指标 常量工具类
 */
public class ExperienceUtil {

    public static Integer SOURCE_SYS = 2;

    public static String BIZ_KEY = "bizKey";

    /**
     * 指标类型 正常
     */
    public static String LOG_TYPE_NOMAL = "ty_0";

    /**
     * 指标类型 阻碍
     */
    public static String LOG_TYPE_ZA = "ty_1";
    /**
     * 指标类型 卡单
     */
    public static String LOG_TYPE_KD = "ty_2";
    /**
     * 指标类型 任务开始
     */
    public static String LOG_TYPE_RW_BEGIN = "ty_3";
    /**
     * 指标类型 任务结束
     */
    public static String LOG_TYPE_RW_END = "ty_4";
    /**
     * 指标类型 任务类型 开始与结束在同一个埋点中
     */
    public static String LOG_TYPE_RW = "ty_5";


    /**
     * 分拣业务
     */
    public static Integer SORTING_BIZ_TYPE = 9901;
    /**
     * 分拣业务
     */
    public static Integer SORTING_OPERATE_TYPE = 990101;

    /**
     * 发货业务
     */
    public static Integer SEND_BIZ_TYPE = 9902;
    /**
     * 发货业务
     */
    public static Integer SEND_OPERATE_TYPE = 990201;


    public static BusinessLogProfiler bulidBusinessLogProfiler(Integer bizType, Integer operateType, String bizKey){
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(SOURCE_SYS);
        businessLogProfiler.setBizType(bizType);
        businessLogProfiler.setOperateType(operateType);
        Map<String, Object> businessLogProfilerRequest = new HashMap<String, Object>();
        businessLogProfilerRequest.put(ExperienceUtil.BIZ_KEY,bizKey);
        businessLogProfiler.setOperateRequest(JsonHelper.toJson(businessLogProfilerRequest));
        businessLogProfiler.setLogType (LOG_TYPE_NOMAL);
        return  businessLogProfiler;
    }

}
