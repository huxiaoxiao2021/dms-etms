package com.jd.bluedragon.distribution.eclpPackage.manager;

import com.jd.bluedragon.distribution.api.request.ThirdWaybillNoRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.ThirdWaybillNoResult;

/**
 * @ClassName EclpLwbB2bPackageItemManager
 * @Description
 * @Author wyh
 * @Date 2020/7/20 14:24
 **/
public interface EclpLwbB2bPackageItemManager {

    String findSellerPackageCode(String sellerPackageNo);
    InvokeResult<ThirdWaybillNoResult> searchPackageNoByThirdWaybillNo(ThirdWaybillNoRequest request);
}
