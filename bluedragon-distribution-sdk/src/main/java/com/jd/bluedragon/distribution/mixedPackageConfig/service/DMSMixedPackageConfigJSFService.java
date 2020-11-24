package com.jd.bluedragon.distribution.mixedPackageConfig.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.MixedSite;
import com.jd.bluedragon.distribution.mixedPackageConfig.domain.PrintQueryRequest;

/**
 * 集包地对外接口
 *
 * @author: hujiping
 * @date: 2020/10/28 17:38
 */
public interface DMSMixedPackageConfigJSFService {

    /**
     * 查询面单集包地
     * @param request
     * @return
     */
    InvokeResult<MixedSite> queryMixedSiteCodeForPrint(PrintQueryRequest request);

}
