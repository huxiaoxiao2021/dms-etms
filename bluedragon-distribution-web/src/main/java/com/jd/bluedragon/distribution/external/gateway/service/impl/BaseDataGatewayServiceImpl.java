package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.external.gateway.service.BaseDataGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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

    private Logger log = Logger.getLogger(BaseDataGatewayServiceImpl.class);

    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

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

    @Override
    public JdCResponse<List<BaseDataDictDto>> getBaseDictByTypeGroups(List<Integer> typeGroups) {
        JdCResponse<List<BaseDataDictDto>> response = new JdCResponse<>();
        if (CollectionUtils.isEmpty(typeGroups)) {
            response.toFail("参数错误");
            return response;
        }
        try {
            List<BaseDataDictDto> result = new ArrayList<>();
            for (Integer typeGroup : typeGroups) {
                List<BaseDataDictDto> dataDictList = this.beanHandler(baseService.getBaseDictionaryTree(typeGroup));
                if (dataDictList != null) {
                    result.addAll(dataDictList);
                }
            }
            response.toSucceed();
            response.setData(result);
        } catch (Exception e) {
            log.error("[网关接口]查询基础字典信息时发生异常，request:" + JsonHelper.toJson(typeGroups), e);
            response.toError("[网关接口]查询基础字典信息时发生异常");
        }
        return response;
    }

    private List<BaseDataDictDto> beanHandler(List<BaseDataDict> baseDataDictList) {
        if (baseDataDictList != null && baseDataDictList.size() > 0) {
            List<BaseDataDictDto> result = new ArrayList<>(baseDataDictList.size());
            for (BaseDataDict baseDataDict : baseDataDictList) {
                BaseDataDictDto dataDict = new BaseDataDictDto();
                dataDict.setId(baseDataDict.getId());
                dataDict.setTypeCode(baseDataDict.getTypeCode());
                dataDict.setTypeName(baseDataDict.getTypeName());
                dataDict.setTypeGroup(baseDataDict.getTypeGroup());
                dataDict.setParentId(baseDataDict.getParentId());
                result.add(dataDict);
            }
            return result;
        }
        return null;
    }
}
