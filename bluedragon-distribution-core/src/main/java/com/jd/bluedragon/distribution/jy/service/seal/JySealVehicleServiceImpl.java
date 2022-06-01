package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

@Service
@Slf4j
public class JySealVehicleServiceImpl implements JySealVehicleService {
    @Autowired
    JySendSealCodeService jySendSealCodeService;
    @Autowired
    JySendAggsService jySendAggsService;
    @Autowired
    JyTransportManager jyTransportManager;
    @Autowired
    JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    JdiTransWorkWSManager jdiTransWorkWSManager;


    @Override
    public InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        //根据运输任务查询 sealcode模型
        List<String> sendCodeList = jySendSealCodeService.selectSealCodeByBizId(sealCodeReq.getSendVehicleBizId());
        if (sendCodeList != null && sendCodeList.size() > 0) {
            SealCodeResp sealCodeResp = new SealCodeResp();
            sealCodeResp.setSealCodeList(sendCodeList);
            sealCodeResp.setVehicleNumber(sealCodeReq.getVehicleNumber());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealCodeResp);
        }
        return new InvokeResult(RESULT_NO_FOUND_DATA_CODE, RESULT_NO_FOUND_DATA_MESSAGE);
    }

    @Override
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        JyBizTaskSendVehicleDetailEntity detailEntity = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleInfoReq.getSendVehicleDetailBizId());
        if (ObjectHelper.isEmpty(detailEntity)){
            return new InvokeResult(RESULT_NO_FOUND_DATA_CODE, RESULT_NO_FOUND_DATA_MESSAGE);
        }
        SealVehicleInfoResp sealVehicleInfoResp = new SealVehicleInfoResp();
        //查询已扫描货物的重量和体积
        JySendAggsEntity jySendAggsEntity = jySendAggsService.getVehicleSendStatistics(sealVehicleInfoReq.getSendVehicleBizId());
        if (ObjectHelper.isNotNull(jySendAggsEntity)) {
            sealVehicleInfoResp.setWeight(String.valueOf(jySendAggsEntity.getTotalScannedWeight()));
            sealVehicleInfoResp.setVolume(String.valueOf(jySendAggsEntity.getTotalScannedVolume()));
        }
        BigQueryOption queryOption = new BigQueryOption();
        queryOption.setQueryTransWorkItemDto(Boolean.TRUE);
        BigTransWorkItemDto bigTransWorkItemDto = jdiTransWorkWSManager.queryTransWorkItemByOptionWithRead(detailEntity.getTransWorkItemCode(), queryOption);
        if (ObjectHelper.isNotNull(bigTransWorkItemDto) && ObjectHelper.isNotNull(bigTransWorkItemDto.getTransWorkItemDto())) {
            TransWorkItemDto transWorkItemDto =bigTransWorkItemDto.getTransWorkItemDto();
            sealVehicleInfoResp.setTransportCode(transWorkItemDto.getTransportCode());
            sealVehicleInfoResp.setVehicleNumber(transWorkItemDto.getVehicleNumber());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealVehicleInfoResp);
        }
        return new InvokeResult(RESULT_NO_FOUND_BY_TRANS_WOEK_ITEM_CODE, RESULT_NO_FOUND_BY_TRANS_WOEK_ITEM_MESSAGE);
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
