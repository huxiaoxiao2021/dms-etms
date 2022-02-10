package com.jd.bluedragon.distribution.auto;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bluedragon.core.base.AutoDistJsfManager;
import com.jd.bluedragon.distribution.command.JdPageResult;
import com.jd.bluedragon.distribution.command.JdResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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
    public JdResult<Boolean> supplementSiteCode(String barCode, Integer siteCode, Integer createSiteCode, String operatorErp) {
        BaseDmsAutoJsfResponse<Object> response = autoDistJsfManager.supplementSiteCode(barCode, siteCode);
        JdResult<Boolean> result = new JdPageResult<>();
        if (response != null) {
            result.setMessage(response.getStatusMessage());
        }
        return result;
    }

}
