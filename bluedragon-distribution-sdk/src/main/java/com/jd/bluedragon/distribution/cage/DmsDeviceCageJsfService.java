package com.jd.bluedragon.distribution.cage;


import com.jd.bluedragon.distribution.cage.request.CollectPackageReq;
import com.jd.bluedragon.distribution.cage.response.CollectPackageResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface DmsDeviceCageJsfService {

    InvokeResult<CollectPackageResp> cage(CollectPackageReq req);
}
