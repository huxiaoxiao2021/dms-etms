package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.AviationSendTaskListReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.SendTaskBindReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.SendTaskUnbindReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendTaskListRes;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyAviationRailwaySendSealServiceImpl;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwaySendSealGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:14
 * @Description
 */

@Service
public class JyAviationRailwaySendSealGatewayServiceImpl implements JyAviationRailwaySendSealGatewayService {

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwaySendSealGatewayServiceImpl.class);




    @Autowired
    private JyAviationRailwaySendSealServiceImpl jyAviationRailwaySendSealService;

    @Autowired
    private BaseParamValidateService baseParamValidateService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.scanTypeOptions",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> scanTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<AviationSendTaskListRes> pageFetchAviationSendTaskList(AviationSendTaskListReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchAviationSendTaskList:航空发货任务列表分页查询服务：";
        try{
           //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            baseParamValidateService.checkPdaPage(request.getPageNo(), request.getPageSize());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.pageFetchAviationSendTaskList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "航空发货任务列表分页查询服务异常", null);//500+非自定义异常
        }
    }



    @Override
    public JdCResponse<Void> sendTaskBinding(SendTaskBindReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.sendTaskBinding:摆渡任务绑定空铁任务服务：";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "被绑任务bizId为空", null);
            }
            if(CollectionUtils.isEmpty(request.getSendTaskBindDtoList())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "需绑任务信息为空", null);
            }
            if(request.getSendTaskBindDtoList().size() > 100) {
                //todo 风险规避，PDA不限制数量
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "最多绑定100个任务", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION_RAILWAY.getCode());
            return retJdCResponse(jyAviationRailwaySendSealService.sendTaskBinding(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务绑定空铁任务服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<Void> sendTaskUnbinding(SendTaskUnbindReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.sendTaskUnbinding:摆渡任务解绑空铁任务服务：";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "被解绑任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getUnbindBizId()) || StringUtils.isBlank(request.getUnbindDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "需解绑任务信息为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION_RAILWAY.getCode());
            return retJdCResponse(jyAviationRailwaySendSealService.sendTaskUnbinding(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务解绑空铁任务服务异常", null);//500+非自定义异常
        }
    }
}
