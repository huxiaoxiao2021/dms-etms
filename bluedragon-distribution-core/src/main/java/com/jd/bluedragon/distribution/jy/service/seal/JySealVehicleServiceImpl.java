package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.tms.basic.dto.TransportResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JySealVehicleServiceImpl implements JySealVehicleService{
    @Autowired
    private NewSealVehicleService newsealVehicleService;



    @Override
    public InvokeResult<SealCodeResponse> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        //根据运输任务查询 sealcode模型
        return null;
    }

    @Override
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        return null;
    }

    @Override
    public InvokeResult<TransportResp> getTransportResourceByTransCode(TransportReq transportReq) {
        return null;
    }

    @Override
    public InvokeResult checkTransportCode(CheckTransportCodeReq checkTransportCodeReq) {
        return null;
    }

    @Override
    public InvokeResult<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        return null;
    }

    @Override
    public InvokeResult sealVehicle(SealVehicleReq sealVehicleReq) {
        return null;
    }
}
