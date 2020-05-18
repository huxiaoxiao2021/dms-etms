package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.send.response.TransPlanDto;
import com.jd.bluedragon.common.dto.sorting.request.SortingCancelRequest;
import com.jd.bluedragon.common.dto.sorting.request.SortingCheckRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.rest.sorting.SortingResource;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.SortingGatewayService;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/6/12
 */
public class SortingGatewayServiceImpl implements SortingGatewayService {

    @Autowired
    @Qualifier("sortingResource")
    private SortingResource sortingResource;

    @Override
    @BusinessLog(sourceSys = 1,bizType = 2002,operateType = 2003)
    @JProfiler(jKey = "DMSWEB.SortingGatewayServiceImpl.sortingCancel", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse sortingCancel(SortingCancelRequest request) {
        JdCResponse response = new JdCResponse();
        response.toFail("操作失败请联系IT");
        if(request.getUser() == null ){
            response.toFail("参数user不能为空");
            return response;
        }
        if(request.getCurrentOperate() == null){
            response.toFail("参数currentOperate不能为空");
            return response;
        }
        if(StringUtils.isEmpty(request.getPackageCode())){
            response.toFail("参数packageCode不能为空");
            return response;
        }
        SortingRequest params = new SortingRequest();
        params.setPackageCode(request.getPackageCode());
        params.setUserCode(request.getUser().getUserCode());
        params.setUserName(request.getUser().getUserName());
        params.setBusinessType(request.getBusinessType());
        params.setOperateTime(DateUtil.format(request.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        params.setSiteCode(request.getCurrentOperate().getSiteCode());
        params.setSiteName(request.getCurrentOperate().getSiteName());
        if(BusinessUtil.isBoxcode(request.getPackageCode())){
            params.setBoxCode(request.getPackageCode());
        }
        SortingResponse sortingResponse = sortingResource.cancelPackage(params);
        if(Objects.equals(sortingResponse.getCode(),SortingResponse.CODE_OK)){
            response.toSucceed(sortingResponse.getMessage());
            return response;
        }
        response.toFail(sortingResponse.getMessage());
        return response;
    }

    @Override
    @BusinessLog(sourceSys = 1,bizType = 2002,operateType = 20021)
    @JProfiler(jKey = "DMSWEB.SortingGatewayServiceImpl.sortingPostCheck", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdVerifyResponse sortingPostCheck(SortingCheckRequest checkRequest) {
        PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
        pdaOperateRequest.setBoxCode(checkRequest.getBoxCode());
        pdaOperateRequest.setBusinessType(checkRequest.getBusinessType());
        pdaOperateRequest.setIsLoss(checkRequest.getIsLoss());
        if(checkRequest.getIsGather() == null){
            pdaOperateRequest.setIsGather(0);
        }else{
            pdaOperateRequest.setIsGather(checkRequest.getIsGather());
        }
        pdaOperateRequest.setOperateType(checkRequest.getOperateType());
        pdaOperateRequest.setPackageCode(checkRequest.getPackageCode());
        pdaOperateRequest.setReceiveSiteCode(checkRequest.getReceiveSiteCode());
        pdaOperateRequest.setCreateSiteCode(checkRequest.getCurrentOperate().getSiteCode());
        pdaOperateRequest.setCreateSiteName(checkRequest.getCurrentOperate().getSiteName());
        pdaOperateRequest.setOperateTime(DateUtil.format(checkRequest.getCurrentOperate().getOperateTime(),DateUtil.FORMAT_DATE_TIME));
        pdaOperateRequest.setOperateUserCode(checkRequest.getUser().getUserCode());
        pdaOperateRequest.setOperateUserName(checkRequest.getUser().getUserName());

        JdVerifyResponse jdVerifyResponse = new JdVerifyResponse();
        try {
            SortingJsfResponse sortingResponse = sortingResource.check(pdaOperateRequest);
            JdVerifyResponse.MsgBox msgBox = null;
            //校验通过
            if(org.jboss.util.Objects.equals(sortingResponse.getCode(),SortingResponse.CODE_OK)){
                jdVerifyResponse.toSuccess(sortingResponse.getMessage());
                return jdVerifyResponse;
            }
            //已知可以认为是接口异常的 code
            if(org.jboss.util.Objects.equals(sortingResponse.getCode(),SortingResponse.CODE_SERVICE_ERROR)
                    || org.jboss.util.Objects.equals(sortingResponse.getCode(),SortingResponse.CODE_PARAM_ERROR)){
                jdVerifyResponse.toError(sortingResponse.getMessage());
                return jdVerifyResponse;
            }
            //业务类提示
            if(sortingResponse.getCode() >= 30000 && sortingResponse.getCode() <= 40000){
                jdVerifyResponse.toSuccess();
                msgBox = new JdVerifyResponse.MsgBox(MsgBoxTypeEnum.CONFIRM,sortingResponse.getCode(),sortingResponse.getMessage());
                //暂未做： 分拣理货  CODE_39123 pda端有特殊提示 确认是否可以不做
                jdVerifyResponse.addBox(msgBox);
                return jdVerifyResponse;
            }
            //业务类 强制拦截
            msgBox = new JdVerifyResponse.MsgBox(MsgBoxTypeEnum.INTERCEPT,sortingResponse.getCode(),sortingResponse.getMessage());
            jdVerifyResponse.toSuccess();
            jdVerifyResponse.addBox(msgBox);
        } catch (Exception e) {
            jdVerifyResponse.toError(e.getMessage());
        }
        return jdVerifyResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SortingGatewayServiceImpl.getWaybillCodes", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<String>> getWaybillCodes(String boxCode){
        JdCResponse<List<String>> res=new JdCResponse<>();
        res.toSucceed();

        if(StringUtils.isEmpty(boxCode)){
            res.toFail("箱号不能为空");
            return res;
        }

        InvokeResult<List<String>> rs= sortingResource.getWaybillCodes(boxCode);
        if (rs.getCode()==InvokeResult.RESULT_SUCCESS_CODE){
            res.setData(rs.getData());
        }else {
            res.setCode(rs.getCode());
            res.setMessage(rs.getMessage());
        }

        return res;
    }
}
