package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedict.response.BaseDict;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.external.gateway.service.DmsBaseDictGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/9/24
 */
public class DmsBaseDictGatewayServiceImpl implements DmsBaseDictGatewayService {

    private final static Logger logger = LoggerFactory.getLogger(DmsBaseDictGatewayServiceImpl.class);

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseDictGatewayServiceImpl.queryListByParentId",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BaseDict>> queryListByParentId(Integer parentId) {
        JdCResponse<List<BaseDict>> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        List<DmsBaseDict> list = dmsBaseDictService.queryListByParentId(parentId);
        if(CollectionUtils.isEmpty(list)){
            jdCResponse.toError("没有配置列表数据！");
            return jdCResponse;
        }
        List<BaseDict> data = new ArrayList<>();
        for(DmsBaseDict item : list){
            BaseDict baseDict = new BaseDict();
            BeanUtils.copyProperties(item,baseDict);
            data.add(baseDict);
        }
        jdCResponse.setData(data);
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsBaseDictGatewayServiceImpl.queryLowerLevelListByTypeCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<BaseDict>> queryLowerLevelListByTypeCode(Integer typeCode) {
        JdCResponse<List<BaseDict>> jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        List<DmsBaseDict> list = dmsBaseDictService.queryLowerLevelListByTypeCode(typeCode);
        if(CollectionUtils.isEmpty(list)){
            jdCResponse.toError("没有配置列表数据！");
            return jdCResponse;
        }
        List<BaseDict> data = new ArrayList<>();
        for(DmsBaseDict item : list){
            BaseDict baseDict = new BaseDict();
            BeanUtils.copyProperties(item,baseDict);
            data.add(baseDict);
        }
        jdCResponse.setData(data);
        return jdCResponse;
    }
}
