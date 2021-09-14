package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.DmsFuncSwitchDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigResponse;

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
    FuncSwitchConfigResponse<List<DmsFuncSwitchDto>> getSiteFilterStatus(Map<String,Object> siteCodeMap);

    /**
     * 判断是否有装车或卸车的大宗权限
     * @param createSiteId 操作站点
     * @param userErp 用户erp
     * @param menuCode 操作菜单
     * @return
     */
    InvokeResult<Boolean> hasInspectOrSendFunction(Integer createSiteId, Integer menuCode, String userErp);

}

