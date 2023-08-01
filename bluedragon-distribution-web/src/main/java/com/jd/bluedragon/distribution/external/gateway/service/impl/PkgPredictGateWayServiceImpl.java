package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackage;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackageDetailResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanWaybill;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.predict.enums.OperationProductType;
import com.jd.bluedragon.common.dto.predict.request.SendPredictAggsQuery;
import com.jd.bluedragon.common.dto.predict.request.WorkWaveInspectedNotSendDetailsReq;
import com.jd.bluedragon.common.dto.predict.request.WorkWaveInspectedNotSendPackageCountReq;
import com.jd.bluedragon.common.dto.predict.response.WorkWaveInspectedNotSendDetailsResponse;
import com.jd.bluedragon.common.dto.predict.response.WorkWaveInspectedNotSendPackageCountResponse;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.external.gateway.service.PkgPredictGateWayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.realtime.api.predict.IPackagePredictAggsJsfService;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.query.predict.SendPredictToScanPackage;
import com.jdl.jy.realtime.model.vo.predict.InspectedNotSendBarCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;
    

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.PkgPredictGateWayServiceImpl.queryCurrentWorkWaveInspectedNotSendPackageCount", mState = {JProEnum.TP, JProEnum.FunctionError})
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

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.PkgPredictGateWayServiceImpl.getSendPredictToScanPackageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendVehicleToScanPackageDetailResponse> getSendPredictToScanPackageList(SendPredictAggsQuery query) {
        if(log.isInfoEnabled()){
            log.info("发货波次待扫包裹列表入参-{}", JSON.toJSONString(query));
        }
        JdCResponse<SendVehicleToScanPackageDetailResponse> response = new JdCResponse<>();
        response.toSucceed("请求成功!");
        try{
            boolean checkResult = checkParam(query, response);
            if(!checkResult){
                return response;
            }

            JyBizTaskSendVehicleDetailEntity querySendVehicleDetail = new JyBizTaskSendVehicleDetailEntity();
            querySendVehicleDetail.setSendVehicleBizId(query.getSendVehicleBizId());
            List<Long> receiveIds = taskSendVehicleDetailService.getAllSendDest(querySendVehicleDetail);
            if(CollectionUtils.isNotEmpty(receiveIds)){

            }

            com.jdl.jy.realtime.model.query.predict.SendPredictAggsQuery  predictAggsQuery = new com.jdl.jy.realtime.model.query.predict.SendPredictAggsQuery ();
            BeanUtils.copyProperties(query,predictAggsQuery);
            ServiceResult<List<SendPredictToScanPackage>> result = iPackagePredictAggsService.getSendPredictToScanPackageList(predictAggsQuery);
            if(log.isInfoEnabled()){
                log.info("发货波次待扫包裹列表出参-{}", JSON.toJSONString(result));
            }
            if(result == null){
                response.toFail("获取发货波次待扫包裹列表失败!");
                return response;
            }
            if( CollectionUtils.isNotEmpty(result.getData())){
                SendVehicleToScanPackageDetailResponse packageDetailResponse =new SendVehicleToScanPackageDetailResponse();
                packageDetailResponse.setProductType(query.getProductType());
                packageDetailResponse.setProductTypeName(JySendVehicleProductTypeEnum.getNameByCode(query.getProductType()));
                List<SendVehicleToScanPackage> packages = new ArrayList<>();
                for (SendPredictToScanPackage pg : result.getData()) {
                    SendVehicleToScanPackage toScanPackage =  new SendVehicleToScanPackage();
                    toScanPackage.setPackageCode(pg.getPackageCode());
                    toScanPackage.setProductType(pg.getProductType());
                    packages.add(toScanPackage);
                }
                packageDetailResponse.setPackageCodeList(packages);
                response.setData(packageDetailResponse);
            }
        }catch (Exception e){
            log.error("获取发货波次待扫包裹列表异常-param{}",JSON.toJSONString(query),e);
            response.toError("获取发货波次待扫包裹列表异常!");
        }
        return response;
    }

    private boolean checkParam(SendPredictAggsQuery query,JdCResponse<SendVehicleToScanPackageDetailResponse> response){
        if(query == null
                || query.getFlag() == null
                || query.getSiteCode() == null
                || StringUtils.isBlank(query.getProductType())){
            response.toFail("入参不能为空!");
            return false;
        }

        if (query.getPageSize() < 0 || query.getPageNumber() < 1) {
            response.toFail("分页参数错误");
            return false;
        }

        if (StringUtils.isBlank(query.getSendVehicleBizId())) {
            response.toFail("请选择发车任务！");
            return false;
        }
        return true;
    }
}
