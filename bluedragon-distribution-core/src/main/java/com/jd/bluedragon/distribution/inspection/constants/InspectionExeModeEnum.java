package com.jd.bluedragon.distribution.inspection.constants;

/**
 * @ClassName InspectionExeModeEnum
 * @Description
 * @Author wyh
 * @Date 2020/9/24 20:52
 **/
public enum InspectionExeModeEnum {

    /**
     * 按运单或包裹验货
     */
    NONE_SPLIT_MODE,

    /**
     * 运单包裹分页拆分验货
     */
    PACKAGE_PAGE_MODE,

    /**
     * 初始化拆分任务模式
     */
    INIT_SPLIT_MODE,
}
