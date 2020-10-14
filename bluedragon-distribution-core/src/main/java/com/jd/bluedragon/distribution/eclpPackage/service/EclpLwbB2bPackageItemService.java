package com.jd.bluedragon.distribution.eclpPackage.service;

/**
 * @ClassName EclpLwbB2bPackageItemService
 * @Description
 * @Author wyh
 * @Date 2020/7/20 14:21
 **/
public interface EclpLwbB2bPackageItemService {

    /**
     * 根据三方运单号返回京东包裹号
     *
     * @param sellerPackageNo 三方运单号
     * @return
     */
    String findSellerPackageCode(String sellerPackageNo);
}
