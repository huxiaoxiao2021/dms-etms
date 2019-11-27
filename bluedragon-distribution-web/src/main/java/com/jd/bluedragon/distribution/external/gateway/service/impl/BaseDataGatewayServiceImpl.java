package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.external.gateway.service.BaseDataGatewayService;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/9/10
 */
public class BaseDataGatewayServiceImpl implements BaseDataGatewayService {


    @Resource
    private BaseResource baseResource;

    @Override
    @JProfiler(jKey = "DMSWEB.BaseDataGatewayServiceImpl.getBaseDictionaryTree",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BaseDataDictDto>> getBaseDictionaryTree(int typeGroup){
        InvokeResult<List<BaseDataDict>> invokeResult = baseResource.getBaseDictionaryTree(typeGroup);
        JdCResponse<List<BaseDataDictDto>> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        if(InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
            jdCResponse.toError(invokeResult.getMessage());
            return jdCResponse;
        }
        List<BaseDataDictDto> dataDictDtos = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(invokeResult.getData())){
            for(BaseDataDict item : invokeResult.getData()){
                BaseDataDictDto dto = new BaseDataDictDto();
                dto.setId(item.getId());
                dto.setTypeCode(item.getTypeCode());
                dto.setTypeGroup(item.getTypeGroup());
                dto.setTypeName(item.getTypeName());
                dataDictDtos.add(dto);
            }
        }
        jdCResponse.setData(dataDictDtos);
        return jdCResponse;
    }
}
