package com.jd.bluedragon.distribution.eclpPackage.service.impl;

import com.jd.bluedragon.distribution.eclpPackage.manager.EclpLwbB2bPackageItemManager;
import com.jd.bluedragon.distribution.eclpPackage.service.EclpLwbB2bPackageItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName EclpLwbB2bPackageItemServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/7/20 14:24
 **/
@Service("eclpLwbB2bPackageItemService")
public class EclpLwbB2bPackageItemServiceImpl implements EclpLwbB2bPackageItemService {

    @Autowired
    private EclpLwbB2bPackageItemManager eclpLwbB2bPackageItemManager;

    @Override
    public String findSellerPackageCode(String sellerPackageNo) {
        return eclpLwbB2bPackageItemManager.findSellerPackageCode(sellerPackageNo);
    }
}
