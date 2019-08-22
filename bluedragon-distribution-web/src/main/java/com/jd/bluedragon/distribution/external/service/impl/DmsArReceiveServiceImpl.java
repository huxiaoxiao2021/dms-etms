package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.service.DmsArReceiveService;
import com.jd.bluedragon.distribution.rest.transport.ArReceiveResource;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceive;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceiveRequest;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.domain.ListResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsArReceiveService")
public class DmsArReceiveServiceImpl implements DmsArReceiveService {

    @Autowired
    @Qualifier("arReceiveResource")
    private ArReceiveResource arReceiveResource;


    @Override
    @JProfiler(jKey = "DMSWEB.DmsArReceiveServiceImpl.getArSendRegisterByBarcode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<ArSendRegister> getArSendRegisterByBarcode(String barcode) {
        return arReceiveResource.getArSendRegisterByBarcode(barcode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsArReceiveServiceImpl.getARWaitReceive", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public ListResponse<ArWaitReceive> getARWaitReceive(ArWaitReceiveRequest request) {
        return arReceiveResource.getARWaitReceive(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsArReceiveServiceImpl.getArSendRegisterByTransInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<List<ArSendRegister>> getArSendRegisterByTransInfo(Integer transType, String transName, String siteOrder, Date sendDate) {
        return arReceiveResource.getArSendRegisterByTransInfo(transType, transName, siteOrder, sendDate);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsArReceiveServiceImpl.getArSendRegisterListByParam", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<List<ArSendRegister>> getArSendRegisterListByParam(Integer transType, String transName, String siteOrder, Date sendDate) {
        return arReceiveResource.getArSendRegisterListByParam(transType, transName, siteOrder, sendDate);
    }

}
