package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.service.DmsSiteService;
import com.jd.bluedragon.distribution.rest.base.SiteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author lixin39
 * @date 2018/11/9
 */
@Service("dmsSiteService")
public class DmsSiteServiceImpl implements DmsSiteService {

    @Autowired
    @Qualifier("siteResource")
    private SiteResource siteResource;

    @Override
    public RouteTypeResponse getCapacityCodeInfo(String capacityCode) {
        return siteResource.getCapacityCodeInfo(capacityCode);
    }

    @Override
    public InvokeResult<CreateAndReceiveSiteInfo> getSitesInfoBySendCode(String sendCode) {
        return siteResource.getSitesInfoBySendCode(sendCode);
    }
}
