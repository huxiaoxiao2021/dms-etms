package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.box.response.BoxDto;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.box.BoxResource;
import com.jd.bluedragon.external.gateway.service.BoxGatewayService;
import org.apache.commons.collections.CollectionUtils;

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

    @Override
    public JdCResponse<BoxDto> boxValidation(String boxCode, Integer operateType) {
        BoxResponse boxResponse = boxResource.validation(boxCode,operateType);
        JdCResponse<BoxDto> jdResponse = new JdCResponse<>();
        if(Objects.equals(boxResponse.getCode(),BoxResponse.CODE_OK)){
            BoxDto boxDto = packageBoxDto(boxResponse);
            jdResponse.toSucceed();
            jdResponse.setData(boxDto);
        }else{
            jdResponse.toError(boxResponse.getMessage());
        }

        return jdResponse;
    }

    @Override
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
            jdCResponse.setData(boxDtoList);
            jdCResponse.setMessage(invokeResult.getMessage());
        }
        jdCResponse.toError(invokeResult.getMessage());
        return jdCResponse;
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
