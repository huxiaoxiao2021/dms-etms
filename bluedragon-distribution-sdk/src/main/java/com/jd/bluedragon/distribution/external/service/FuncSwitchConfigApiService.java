package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.external.domain.DmsFuncSwitchDto;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;
import java.util.Map;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/24 15:50
 */
public interface FuncSwitchConfigApiService {

    /**
     * 通过站点编码,获取拦截状态
     * @param siteCodeMap
     * @return
     */
    JdResponse<List<DmsFuncSwitchDto>> getSiteFilterStatus(Map<String,Integer> siteCodeMap);
}
    
