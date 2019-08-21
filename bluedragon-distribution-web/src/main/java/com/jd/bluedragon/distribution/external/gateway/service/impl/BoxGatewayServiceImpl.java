package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.box.response.BoxDto;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.BoxGatewayService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 箱相关业务
 * @author  xumigen
 * @date : 2019/7/3
 */
public class BoxGatewayServiceImpl implements BoxGatewayService {

    @Resource
    private BoxResource boxResource;


    @Autowired
    private BaseService baseService;

    @Override
    @JProfiler(jKey = "DMSWEB.BoxGatewayServiceImpl.boxValidation",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<BoxDto> boxValidation(String boxCode, Integer operateType) {
        BoxResponse boxResponse = boxResource.validation(boxCode,operateType);
        JdVerifyResponse<BoxDto> jdVerifyResponse = new JdVerifyResponse<>();
        if(!Objects.equals(boxResponse.getCode(),BoxResponse.CODE_OK)){
            jdVerifyResponse.toError(boxResponse.getMessage());
            return jdVerifyResponse;
        }
        jdVerifyResponse.toSuccess();
        BoxDto boxDto = packageBoxDto(boxResponse);
        jdVerifyResponse.setData(boxDto);
        //判断加盟 给页面返回提示类型信息
        BaseStaffSiteOrgDto dto = baseService.queryDmsBaseSiteByCode(String.valueOf(boxDto.getReceiveSiteCode()));
        if(dto != null && BusinessUtil.isAllianceBusiSite(dto.getSiteType(),dto.getSubType())){
            jdVerifyResponse.addPromptBox(0,"派送至加盟商请复重！");
        }
        return jdVerifyResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.BoxGatewayServiceImpl.getGroupEffectiveBoxes",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BoxDto>> getGroupEffectiveBoxes(String boxCode){
        InvokeResult<List<String>> invokeResult = boxResource.getAllGroupBoxes(boxCode);
        JdCResponse<List<BoxDto>> jdCResponse = new JdCResponse<>();
        if(invokeResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE && CollectionUtils.isNotEmpty(invokeResult.getData())){
            List<String> boxLists = invokeResult.getData();
            List<BoxDto> boxDtoList = Lists.newArrayList();
            for(String item : boxLists){
                BoxResponse boxResponse = boxResource.validation(item,2);
                if(Objects.equals(boxResponse.getCode(),BoxResponse.CODE_OK)){
                    boxDtoList.add(packageBoxDto(boxResponse));
                }
            }
            jdCResponse.toSucceed();
            jdCResponse.setData(boxDtoList);
            jdCResponse.setMessage(invokeResult.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(invokeResult.getMessage());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.BoxGatewayServiceImpl.getBoxInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<BoxDto> getBoxInfo(String boxCode) {
        JdCResponse<BoxDto> jdResponse = new JdCResponse<>();
        BoxResponse boxResponse= boxResource.get(boxCode);

        if(boxResponse.getCode().equals(BoxResponse.CODE_OK)){
            BoxDto boxDto = packageBoxDto(boxResponse);
            jdResponse.toSucceed();
            jdResponse.setData(boxDto);
        }else{
            jdResponse.toError(boxResponse.getMessage());
        }

        return jdResponse;
    }

    /**
     * 封装对象
     * @param boxResponse boxResponse
     * @return BoxDto
     */
    private BoxDto packageBoxDto(BoxResponse boxResponse) {
        BoxDto boxDto = new BoxDto();
        boxDto.setBoxCode(boxResponse.getBoxCode());
        boxDto.setCreateSiteCode(boxResponse.getCreateSiteCode());
        boxDto.setCreateSiteName(boxResponse.getCreateSiteName());
        boxDto.setReceiveSiteCode(boxResponse.getReceiveSiteCode());
        boxDto.setReceiveSiteName(boxResponse.getReceiveSiteName());
        boxDto.setSiteType(boxResponse.getSiteType());
        boxDto.setTransportType(boxResponse.getTransportType());
        return boxDto;
    }
}
