package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackage;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanWaybill;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.predict.enums.OperationProductType;
import com.jd.bluedragon.common.dto.predict.request.WorkWaveInspectedNotSendDetailsReq;
import com.jd.bluedragon.common.dto.predict.request.WorkWaveInspectedNotSendPackageCountReq;
import com.jd.bluedragon.common.dto.predict.response.WorkWaveInspectedNotSendDetailsResponse;
import com.jd.bluedragon.common.dto.predict.response.WorkWaveInspectedNotSendPackageCountResponse;
import com.jd.bluedragon.external.gateway.service.PkgPredictGateWayService;
import com.jdl.jy.realtime.api.predict.IPackagePredictAggsJsfService;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.vo.predict.InspectedNotSendBarCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service

public class PkgPredictGateWayServiceImpl implements PkgPredictGateWayService {
    private Logger log = LoggerFactory.getLogger(PkgPredictGateWayServiceImpl.class);

    @Autowired
    IPackagePredictAggsJsfService iPackagePredictAggsService;


    @Override
    public JdCResponse<WorkWaveInspectedNotSendPackageCountResponse> queryCurrentWorkWaveInspectedNotSendPackageCount(WorkWaveInspectedNotSendPackageCountReq req) {
        if (req.getQueryTime() == null) {
            req.setQueryTime(new Date());
        }

        JdCResponse<WorkWaveInspectedNotSendPackageCountResponse> gateWayrResponse = new JdCResponse<>(200, "ok");
        try {
            com.jdl.jy.realtime.model.query.predict.WorkWaveInspectedNotSendPackageCountReq r = new com.jdl.jy.realtime.model.query.predict.WorkWaveInspectedNotSendPackageCountReq();
            BeanUtils.copyProperties(req, r);
            ServiceResult<com.jdl.jy.realtime.model.vo.predict.WorkWaveInspectedNotSendPackageCountResponse> result = iPackagePredictAggsService.queryCurrentWorkWaveInspectedNotSendPackageCount(r);
            if (Objects.equals(result.getCode(), 200)) {
                WorkWaveInspectedNotSendPackageCountResponse response = new WorkWaveInspectedNotSendPackageCountResponse();
                BeanUtils.copyProperties(result.getData(), response);
                gateWayrResponse.setData(response);
                return gateWayrResponse;
            } else {
                return new JdCResponse<>(result.getCode(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("queryCurrentWorkWaveInspectedNotSendPackageCount error" + JSONObject.toJSONString(req), e);
            return new JdCResponse<>(500, "服务器异常");
        }
    }

    @Override
    public JdCResponse<WorkWaveInspectedNotSendDetailsResponse> queryCurrentWorkWaveInspectedNotSendWaybillsByPage(WorkWaveInspectedNotSendDetailsReq req) {
        if (req.getQueryTime() == null) {
            req.setQueryTime(new Date());
        }
        JdCResponse<WorkWaveInspectedNotSendDetailsResponse> gateWayrResponse = new JdCResponse<>(200, "ok");
        try {
            com.jdl.jy.realtime.model.query.predict.WorkWaveInspectedNotSendDetailsReq workWaveInspectedNotSendDetailsReq = new com.jdl.jy.realtime.model.query.predict.WorkWaveInspectedNotSendDetailsReq();
            BeanUtils.copyProperties(req, workWaveInspectedNotSendDetailsReq);
            ServiceResult<com.jdl.jy.realtime.model.vo.predict.WorkWaveInspectedNotSendDetailsResponse> workWaveInspectedNotSendDetailsResponseServiceResult = iPackagePredictAggsService.queryCurrentWorkWaveInspectedNotSendWaybillsByPage(workWaveInspectedNotSendDetailsReq);
            WorkWaveInspectedNotSendDetailsResponse response = new WorkWaveInspectedNotSendDetailsResponse();
            response.setTotal(workWaveInspectedNotSendDetailsResponseServiceResult.getData().getTotal());
            response.setPageSize(workWaveInspectedNotSendDetailsResponseServiceResult.getData().getPageSize());
            List<SendVehicleToScanWaybill> barCodes = new ArrayList<>();
            for (InspectedNotSendBarCode waybillCode : workWaveInspectedNotSendDetailsResponseServiceResult.getData().getWaybillCodes()) {

                SendVehicleToScanWaybill waybill = new SendVehicleToScanWaybill();
                waybill.setWaybillCode(waybillCode.getBarCode());
                waybill.setProductType(waybillCode.getProductType());
                ArrayList<LabelOption> labelOptions = new ArrayList<>();
                LabelOption labelOption = new LabelOption();
                labelOption.setCode(BarCodeLabelOptionEnum.PRODUCT_TYPE.getCode());
                labelOption.setOrder(1);
                labelOption.setName(OperationProductType.nameByType(waybill.getProductType()));
                labelOptions.add(labelOption);

                waybill.setTags(labelOptions);
                barCodes.add(waybill);
            }
            response.setWaybillCodes(barCodes);
            gateWayrResponse.setData(response);
            return gateWayrResponse;

        } catch (Exception e) {
            log.error("queryCurrentWorkWaveInspectedNotSendWaybillsByPage error" + JSONObject.toJSONString(req), e);
            return new JdCResponse<>(500, "服务器异常");
        }
    }

    @Override
    public JdCResponse<WorkWaveInspectedNotSendDetailsResponse> queryCurrentWorkWaveInspectedNotSendPackagesByPage(WorkWaveInspectedNotSendDetailsReq req) {
        if (req.getQueryTime() == null) {
            req.setQueryTime(new Date());
        }
        JdCResponse<WorkWaveInspectedNotSendDetailsResponse> gateWayrResponse = new JdCResponse<>(200, "ok");
        try {
            com.jdl.jy.realtime.model.query.predict.WorkWaveInspectedNotSendDetailsReq workWaveInspectedNotSendDetailsReq = new com.jdl.jy.realtime.model.query.predict.WorkWaveInspectedNotSendDetailsReq();
            BeanUtils.copyProperties(req, workWaveInspectedNotSendDetailsReq);
            ServiceResult<com.jdl.jy.realtime.model.vo.predict.WorkWaveInspectedNotSendDetailsResponse> workWaveInspectedNotSendDetailsResponseServiceResult = iPackagePredictAggsService.queryCurrentWorkWaveInspectedNotSendPackagesByPage(workWaveInspectedNotSendDetailsReq);
            WorkWaveInspectedNotSendDetailsResponse response = new WorkWaveInspectedNotSendDetailsResponse();
            response.setTotal(workWaveInspectedNotSendDetailsResponseServiceResult.getData().getTotal());
            response.setPageSize(workWaveInspectedNotSendDetailsResponseServiceResult.getData().getPageSize());
            List<SendVehicleToScanPackage> barCodes = new ArrayList<>();
            for (InspectedNotSendBarCode packageCode : workWaveInspectedNotSendDetailsResponseServiceResult.getData().getPackageCodes()) {

                SendVehicleToScanPackage pkg = new SendVehicleToScanPackage();
                pkg.setPackageCode(packageCode.getBarCode());
                pkg.setProductType(packageCode.getProductType());
                ArrayList<LabelOption> labelOptions = new ArrayList<>();
                LabelOption labelOption = new LabelOption();
                labelOption.setCode(BarCodeLabelOptionEnum.PRODUCT_TYPE.getCode());
                labelOption.setOrder(1);
                labelOption.setName(OperationProductType.nameByType(pkg.getProductType()));
                labelOptions.add(labelOption);
                pkg.setTags(labelOptions);
                barCodes.add(pkg);
            }
            response.setPackageCodes(barCodes);
            gateWayrResponse.setData(response);
            return gateWayrResponse;

        } catch (Exception e) {
            log.error("queryCurrentWorkWaveInspectedNotSendWaybillsByPage error" + JSONObject.toJSONString(req), e);
            return new JdCResponse<>(500, "服务器异常");
        }
    }
}
