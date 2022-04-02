package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @ClassName UnloadVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
@Service
public class UnloadVehicleServiceImpl implements IUnloadVehicleService{

    private static final Logger log = LoggerFactory.getLogger(UnloadVehicleServiceImpl.class);

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Long> unloadScan(UnloadScanRequest request) {
        InvokeResult<Long> result = new InvokeResult<>();


        return result;
    }
}
