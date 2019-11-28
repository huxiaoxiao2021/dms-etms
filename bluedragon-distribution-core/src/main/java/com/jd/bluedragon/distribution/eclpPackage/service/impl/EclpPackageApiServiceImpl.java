package com.jd.bluedragon.distribution.eclpPackage.service.impl;

import com.jd.bluedragon.distribution.eclpPackage.manager.EclpPackageApiManager;
import com.jd.bluedragon.distribution.eclpPackage.service.EclpPackageApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("eclpPackageApiService")
public class EclpPackageApiServiceImpl implements EclpPackageApiService{

    @Autowired
    private EclpPackageApiManager eclpPackageApiManager;

    /**
     * 根据商家ID和商家单号获取一个包裹
     *
     * @param busiId        商家ID
     * @param busiOrderCode 商家单号
     * @return
     */
    @Override
    public String queryPackage(Integer busiId, String busiOrderCode) {
        return eclpPackageApiManager.queryPackage(busiId,busiOrderCode);
    }
}
