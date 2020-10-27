package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.sysConfig.response.SysConfigDto;
import com.jd.bluedragon.distribution.api.response.SysConfigResponse;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.external.gateway.service.SysConfigManageGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SysConfigManageGateWayServiceImpl implements SysConfigManageGateWayService {
    private final Logger logger = LoggerFactory.getLogger(SysConfigManageGateWayServiceImpl.class);

    @Autowired
    BaseResource baseResource;

    @Override
    @JProfiler(jKey = "DMSWEB.SysConfigManageGateWayServiceImpl.getConfigByKey",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<SysConfigDto>> getConfigByKey(String key){
        JdCResponse<List<SysConfigDto>> res=new JdCResponse<>();
        res.toSucceed();

        if(StringUtils.isEmpty(key)){
            res.toFail("查询关键字不能为空");
            return res;
        }

        List<SysConfigResponse> configs=baseResource.getConfigByKey(key);
        if (null !=configs && configs.size()>0){
            String datastr= JsonHelper.toJson(configs);
            res.setData(JsonHelper.fromJson(datastr,new ArrayList<SysConfigDto>().getClass()));
        }

        return res;
    }
}
