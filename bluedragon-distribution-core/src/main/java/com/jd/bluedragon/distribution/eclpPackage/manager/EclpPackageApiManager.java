package com.jd.bluedragon.distribution.eclpPackage.manager;

public interface EclpPackageApiManager {
    /**
     * 根据商家ID和商家单号获取一个包裹
     * @param busiId 商家ID
     * @param busiOrderCode 商家单号
     * @return
     */
    String queryPackage(Integer busiId ,String busiOrderCode);
}
