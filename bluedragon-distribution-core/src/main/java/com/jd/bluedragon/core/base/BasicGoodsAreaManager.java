package com.jd.bluedragon.core.base;


/**
 * 货区编码查询包装类
 *
 * @author hujiping
 * @date 2021/11/15 3:51 下午
 */
public interface BasicGoodsAreaManager {

    /**
     * 根据站点编码和下一站编码查询货区编码
     *
     * @param siteCode
     * @param nextSiteCode
     * @return
     */
    String getGoodsAreaNextSite(Integer siteCode, Integer nextSiteCode);
}
