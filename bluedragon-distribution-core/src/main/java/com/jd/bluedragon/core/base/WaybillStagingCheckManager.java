package com.jd.bluedragon.core.base;

/**
 * @program: bluedragon-distribution
 * @description: 运单暂存类型校验
 * @author: wuming
 * @create: 2021-01-25 11:00
 */
public interface WaybillStagingCheckManager {

    /**
     * 运单是否支持暂存预约校验
     *
     * @param packageCode
     * @param operateSiteCode
     * @return
     */
    boolean stagingCheck(String packageCode, Integer operateSiteCode);
}
