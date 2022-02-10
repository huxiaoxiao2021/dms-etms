package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.autodist.AutoDistJsfService;
import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AutoDistJsfManager")
public class AutoDistJsfManagerImpl implements AutoDistJsfManager {
    @Autowired
    private AutoDistJsfService autoDistJsfService;

    /**
     * 上海亚一的pda专用的包裹补码功能，实现扫描包裹号或者运单号将信息推送给WCS
     * <doc>
     * 上海亚一的pda调用的接口,如果不输入siteCode的值的话，默认传0
     * </doc>
     *
     * @param barCode
     * @param siteCode
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.AutoDistJsfManagerImpl.supplementSiteCode", mState = {JProEnum.TP})
    public BaseDmsAutoJsfResponse<Object> supplementSiteCode(String barCode , Integer siteCode,Integer createSiteCode,String operatorErp) {
        return autoDistJsfService.supplementSiteCode(barCode , siteCode,createSiteCode,operatorErp);
    }
}
