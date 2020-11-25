package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.external.domain.DmsFuncSwitchDto;
import com.jd.bluedragon.distribution.external.service.FuncSwitchConfigApiService;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigResponse;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: liming522
 * @Description: 提供给分拣机同步站点拦截状态的接口
 * @Date: create in 2020/11/24 16:03
 */
@Service("funcSwitchConfigApiService")
public class FuncSwitchConfigApiServiceImpl implements FuncSwitchConfigApiService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Override
    public FuncSwitchConfigResponse<List<DmsFuncSwitchDto>>  getSiteFilterStatus(Map<String,Object> siteCodeMap) {
        CallerInfo info = Profiler.registerInfo("DMS.WEB.FuncSwitchConfigApiServiceImpl.getSiteFilterStatus", false, true);
        FuncSwitchConfigResponse<List<DmsFuncSwitchDto>> response = null;
        Integer siteCode = null;
        try {

            if(siteCodeMap.get("siteCode")== null){
                response.setCode(JdResponse.CODE_SUCCESS);
                response.setMessage("参数siteCode不能为空");
            }
            siteCode  = Integer.valueOf(String.valueOf(siteCodeMap.get("siteCode")));
            response  =  funcSwitchConfigService.getSiteFilterStatus(siteCode);
        }catch (Exception e){
            log.error("站点:{}分拣机拦截状态同步接口异常",siteCode,e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return response;
    }
}
    
