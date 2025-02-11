package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.waybillnocollection.request.DifferentialQueryRequest;
import com.jd.bluedragon.common.dto.waybillnocollection.response.DifferentialQueryResultDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.waybill.WaybillResource;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionRequest;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionResult;
import com.jd.bluedragon.external.gateway.service.DifferentialQueryGatewayService;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * DifferentialQueryGatewayServiceImpl
 * 一车一单发货、组板、建箱差异查询服务
 *
 * @author jiaowenqiang
 * @date 2019/7/5
 */
@Service("differentialQueryGatewayService")
public class DifferentialQueryGatewayServiceImpl implements DifferentialQueryGatewayService {

    @Autowired
    WaybillResource waybillResource;

    /**
     * 差异查询
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DifferentialQueryGatewayServiceImpl.getDifferentialQuery"
            ,jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<DifferentialQueryResultDto> getDifferentialQuery(DifferentialQueryRequest request) {
        JdCResponse<DifferentialQueryResultDto> jdCResponse = new JdCResponse<>();
        DifferentialQueryResultDto differentialQueryResultDto = new DifferentialQueryResultDto();
        if(request == null){
            jdCResponse.toFail("入参不能为空");
            return jdCResponse;
        }

        if (request.getUser().getUserCode()<=0 || StringUtils.isBlank(request.getUser().getUserName()) || request.getCurrentOperate().getSiteCode()<=0 || StringUtils.isBlank(request.getCurrentOperate().getSiteName())){
            jdCResponse.toFail("操作人信息和场地信息都不能为空");
            return jdCResponse;
        }

        InvokeResult<WaybillNoCollectionResult> result = waybillResource.getWaybillNoCollectionInfo(convert(request));
        if (result.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
            differentialQueryResultDto.setPackageCodeList(result.getData().getPackageCodeList());
        }


        jdCResponse.setCode(result.getCode());
        jdCResponse.setMessage(result.getMessage());
        jdCResponse.setData(differentialQueryResultDto);

        return jdCResponse;
    }


    /**
     * 参数转化
     */
    private WaybillNoCollectionRequest convert(DifferentialQueryRequest param) {
        WaybillNoCollectionRequest request = new WaybillNoCollectionRequest();

        request.setQueryCode(param.getQueryCode());
        request.setQueryType(param.getQueryType());
        request.setReceiveSiteCode(param.getReceiveSiteCode());
        request.setOperateTime(DateUtil.getDateStr(param.getCurrentOperate().getOperateTime()));
        request.setSiteCode(param.getCurrentOperate().getSiteCode());
        request.setUserCode(String.valueOf(param.getUser().getUserCode()));
        request.setUserName(param.getUser().getUserName());

        return request;
    }
}
