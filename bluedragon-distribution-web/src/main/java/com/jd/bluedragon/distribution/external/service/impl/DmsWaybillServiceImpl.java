package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.external.service.DmsWaybillService;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
    @JProfiler(jKey = "DMSWEB.DmsWaybillServiceImpl.isCancel", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillSafResponse isCancel(PdaOperateRequest pdaOperateRequest) {
        return waybillResource.isCancel(pdaOperateRequest);
    }

}
