package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.AutoDistJsfManager;
import com.jd.bluedragon.external.gateway.service.AutoDistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AutoDistServiceImpl implements AutoDistService {
    @Autowired
    private AutoDistJsfManager autoDistJsfManager;
    /**
     * 上海亚一的pda专用的包裹补码功能，实现扫描包裹号或者运单号将信息推送给WCS
     * <doc>
     * 上海亚一的pda调用的接口,如果不输入siteCode的值的话，默认传0
     * </doc>
     *
     */
    @Override
    public JdCResponse<Boolean> supplementSiteCode(String barCode, Integer siteCode, Integer createSiteCode, String operatorErp) {
        log.info("自动补码收到参数:barCode={},siteCode={},createSiteCode={},operatorErp={}", barCode, siteCode, createSiteCode, operatorErp);
        BaseDmsAutoJsfResponse<Object> response = autoDistJsfManager.supplementSiteCode(barCode, siteCode);
        if (log.isInfoEnabled()) {
            log.info("自动补码调用jsf返回:barCode={},siteCode={},res={}", barCode, siteCode, JSON.toJSONString(response));
        }
        JdCResponse<Boolean> result = new JdCResponse<>();
        result.toSucceed();
        if (response != null) {
            result.setMessage(response.getStatusMessage());
        }
        return result;
    }

}
