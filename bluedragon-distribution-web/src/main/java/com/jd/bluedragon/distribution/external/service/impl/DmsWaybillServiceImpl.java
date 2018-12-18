package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.external.service.DmsWaybillService;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsWaybillService")
public class DmsWaybillServiceImpl implements DmsWaybillService {

    @Autowired
    @Qualifier("waybillResource")
    private WaybillResource waybillResource;

    @Override
    public WaybillSafResponse isCancel(PdaOperateRequest pdaOperateRequest) {
        return waybillResource.isCancel(pdaOperateRequest);
    }

}
