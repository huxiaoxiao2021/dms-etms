package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;
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
        SealVehicleInfoResp sealVehicleInfoResp = new SealVehicleInfoResp();
        JySendAggsEntity jySendAggsEntity = jySendAggsService.getVehicleSendStatistics(sealVehicleInfoReq.getSendVehicleBizId());
        if (ObjectHelper.isNotNull(jySendAggsEntity)) {
            sealVehicleInfoResp.setWeight(String.valueOf(jySendAggsEntity.getTotalScannedWeight()));
            sealVehicleInfoResp.setVolume(String.valueOf(jySendAggsEntity.getTotalScannedVolume()));
        }
        try {
            //TODO 缺个接口
            CommonDto<BasicVehicleTypeDto> basicVehicleTypeResp = jyTransportManager.getVehicleTypeByVehicleNum(sealVehicleInfoReq.getVehicleNumber());
            if (RESULT_SUCCESS_CODE == basicVehicleTypeResp.getCode()) {
                BasicVehicleTypeDto basicVehicleTypeDto= basicVehicleTypeResp.getData();
                sealVehicleInfoResp.setVehicleNumber(sealVehicleInfoReq.getVehicleNumber());
                sealVehicleInfoResp.setVehicleType(basicVehicleTypeDto.getVehicleType());
                sealVehicleInfoResp.setVehicleTypeName(basicVehicleTypeDto.getVehicleTypeName());
                //sealVehicleInfoResp.setTransportCode(basicVehicleTypeDto.getTr);
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sealVehicleInfoReq);
            }
            return new InvokeResult(basicVehicleTypeResp.getCode(), basicVehicleTypeResp.getMessage());
        } catch (Exception e) {
            log.error("根据车牌号查询运输任务详情异常", e);
            return new InvokeResult(RESULT_EXE_GETCARINFO_BYCARNO_CODE, RESULT_EXE_GETCARINFO_BYCARNO_MESSAGE);
        }
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
