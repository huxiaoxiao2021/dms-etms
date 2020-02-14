package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.queueManagement.request.PdaPlatformReq;
import com.jd.bluedragon.common.dto.queueManagement.request.PlatformCallNumReq;
import com.jd.bluedragon.common.dto.queueManagement.request.PlatformWorkReq;
import com.jd.bluedragon.common.dto.queueManagement.response.PlatformCallNumDto;
import com.jd.bluedragon.common.dto.queueManagement.response.PlatformQueueTaskDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.queueManagement.domain.PdaPlatformRequest;
import com.jd.bluedragon.distribution.queueManagement.domain.PlatformCallNumRequest;
import com.jd.bluedragon.distribution.queueManagement.domain.PlatformWorkRequest;
import com.jd.bluedragon.distribution.rest.queueManagement.QueueManagementResource;
import com.jd.bluedragon.external.gateway.service.QueueManagementGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.intelligent.center.api.common.dto.PdaPlatformInfoResponseDto;
import com.jd.intelligent.center.api.common.dto.PlatformCallNumResponseDto;
import com.jd.intelligent.center.api.common.dto.PlatformQueueTaskResponseDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.common.dto.queueManagement.response.PdaPlatformInfoDto;
import org.springframework.beans.BeanUtils;
import com.jd.bluedragon.distribution.queueManagement.domain.Operator;

import java.util.ArrayList;
import java.util.List;

public class QueueManagementGateWayServiceImpl implements QueueManagementGateWayService {
    private final Logger logger = LoggerFactory.getLogger(QueueManagementGateWayServiceImpl.class);

    @Autowired
    QueueManagementResource queueManagementResource;

    @Override
    @JProfiler(jKey = "DMSWEB.QueueManagementGateWayServiceImpl.getPlatformInfoList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<PdaPlatformInfoDto>> getPlatformInfoList(PdaPlatformReq request){
        JdCResponse<List<PdaPlatformInfoDto>> res=new JdCResponse<>();
        res.toSucceed();

        PdaPlatformRequest platformRequest=new PdaPlatformRequest();
        if(StringUtils.isEmpty(request.getCurrentStationCode())){
            res.toFail("作业区编码不能为空");
            return res;
        }
       if (request.getResourceType() < 1) {
           res.toFail("工作区类型错误");
           return res;
        }

        BeanUtils.copyProperties(request, platformRequest);
        InvokeResult<List<PdaPlatformInfoResponseDto>> resourceRES=queueManagementResource.getPlatformInfoList(platformRequest);
        if (InvokeResult.RESULT_SUCCESS_CODE == resourceRES.getCode()) {
            res.setMessage(resourceRES.getMessage());
            String datastr=JsonHelper.toJson(resourceRES.getData());
            res.setData(JsonHelper.fromJson(datastr,new ArrayList<PdaPlatformInfoDto>().getClass()));
        }
        else {
            res.toFail(resourceRES.getMessage());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.QueueManagementGateWayServiceImpl.getPlatformQueueTaskList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<PlatformQueueTaskDto>> getPlatformQueueTaskList(PdaPlatformReq request){
        JdCResponse<List<PlatformQueueTaskDto>> res=new JdCResponse<>();
        res.toSucceed();

        PdaPlatformRequest platformRequest=new PdaPlatformRequest();
        if(StringUtils.isEmpty(request.getCurrentStationCode())){
            res.toFail("作业区编码不能为空");
            return res;
        }
        if (request.getResourceType() < 1) {
            res.toFail("工作区类型错误");
            return res;
        }

        if (StringUtils.isEmpty(request.getPlatformCode())){
            res.toFail("月台编码不能为空");
            return res;
        }

        BeanUtils.copyProperties(request, platformRequest);
        InvokeResult<List<PlatformQueueTaskResponseDto>> resourceRES=queueManagementResource.getPlatformQueueTaskList(platformRequest);
        if (InvokeResult.RESULT_SUCCESS_CODE == resourceRES.getCode()) {
            res.setMessage(resourceRES.getMessage());
            String datastr=JsonHelper.toJson(resourceRES.getData());
            res.setData(JsonHelper.fromJson(datastr,new ArrayList<PlatformQueueTaskDto>().getClass()));
        }
        else {
            res.toFail(resourceRES.getMessage());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.QueueManagementGateWayServiceImpl.isCoccupyPlatform",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> isCoccupyPlatform(PlatformCallNumReq request){
        JdCResponse<Boolean> res=new  JdCResponse<>();
        res.toSucceed();

        if(StringUtils.isEmpty(request.getPlatformCode()))
        {
            res.toFail("月台编码不能为空");
            return res;
        }

        PlatformCallNumRequest platformCallNumRequest=new PlatformCallNumRequest();
        platformCallNumRequest.setPlatformCode(request.getPlatformCode());
        Operator op=new Operator();
        op.setOperatorUserErp(request.getUser().getUserErp());
        op.setCurrentStationCode(request.getCurrentOperate().getDmsCode());
        op.setResourceTypeEnum(2);
        platformCallNumRequest.setOperatorInfo(op);

        InvokeResult<Boolean> resourceRES=queueManagementResource.isCoccupyPlatform(platformCallNumRequest);
        if (InvokeResult.RESULT_SUCCESS_CODE == resourceRES.getCode()) {
            res.setMessage(resourceRES.getMessage());
            res.setData(resourceRES.getData());
        }
        else {
            res.toFail(resourceRES.getMessage());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.QueueManagementGateWayServiceImpl.callNum",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<PlatformCallNumDto> callNum(PlatformCallNumReq request){
        JdCResponse<PlatformCallNumDto> res=new  JdCResponse<>();
        res.toSucceed();

        if(StringUtils.isEmpty(request.getPlatformCode()))
        {
            res.toFail("月台编码不能为空");
            return res;
        }
        if((StringUtils.isEmpty(request.getFlowCode()) || StringUtils.isEmpty(request.getCarType())) && request.getPlatformWorkTypeEnum()==0)
        {
            res.toFail("流向和车型不能为空");
            return res;
        }

        PlatformCallNumRequest platformCallNumRequest=new PlatformCallNumRequest();
        platformCallNumRequest.setPlatformCode(request.getPlatformCode());
        platformCallNumRequest.setFlowCode(request.getFlowCode());
        platformCallNumRequest.setCarType(request.getCarType());
        platformCallNumRequest.setPlatformWorkTypeEnum(request.getPlatformWorkTypeEnum());
        Operator op=new Operator();
        op.setOperatorUserErp(request.getUser().getUserErp());
        op.setCurrentStationCode(request.getCurrentOperate().getDmsCode());
        op.setResourceTypeEnum(2);
        platformCallNumRequest.setOperatorInfo(op);

        InvokeResult<PlatformCallNumResponseDto> resourceRES=queueManagementResource.callNum(platformCallNumRequest);
        if (InvokeResult.RESULT_SUCCESS_CODE == resourceRES.getCode()) {
            res.setMessage(resourceRES.getMessage());
            String datastr=JsonHelper.toJson(resourceRES.getData());
            res.setData(JsonHelper.fromJson(datastr,PlatformCallNumDto.class));
        }
        else {
            res.toFail(resourceRES.getMessage());
        }

        return res;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.QueueManagementGateWayServiceImpl.platformWorkFeedback",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> platformWorkFeedback(PlatformWorkReq request){
        JdCResponse<Boolean> res=new  JdCResponse<>();
        res.toSucceed();

        if(StringUtils.isEmpty(request.getPlatformCode())){
            res.toFail("月台编码不能为空");
            return res;
        }
        if(StringUtils.isEmpty(request.getQueueTaskCode())){
            res.toFail("排队任务编码不能为空");
            return res;
        }
        if (request.getPlatformWorkTypeEnum()<0){
            res.toFail("作业操作类型不能为空");
            return res;
        }

        PlatformWorkRequest platformWorkRequest=new PlatformWorkRequest();
        platformWorkRequest.setPlatformCode(request.getPlatformCode());
        platformWorkRequest.setQueueTaskCode(request.getQueueTaskCode());
        platformWorkRequest.setPlatformWorkTypeEnum(request.getPlatformWorkTypeEnum());
        Operator op=new Operator();
        op.setOperatorUserErp(request.getUser().getUserErp());
        op.setCurrentStationCode(request.getCurrentOperate().getDmsCode());
        op.setResourceTypeEnum(2);
        platformWorkRequest.setOperatorInfo(op);

        InvokeResult<Boolean> resourceRES=queueManagementResource.platformWorkFeedback(platformWorkRequest);
        if (InvokeResult.RESULT_SUCCESS_CODE == resourceRES.getCode()) {
            res.setMessage(resourceRES.getMessage());
            res.setData(resourceRES.getData());
        }
        else {
            res.toFail(resourceRES.getMessage());
        }

        return res;
    }

}
