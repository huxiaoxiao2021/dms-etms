package com.jd.bluedragon.core.base;

import IceInternal.Ex;
import com.jd.bluedragon.Constants;
import com.jd.etms.api.common.dto.CommonDto;
import com.jd.etms.api.waybillroutelink.WaybillRouteLinkQueryAPI;
import com.jd.etms.api.waybillroutelink.resp.WaybillRouteLinkResp;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/8 17:43
 * @Description:
 */
@Service
public class WaybillRouteLinkQueryManagerImpl implements WaybillRouteLinkQueryManager{

    private static final Logger log = LoggerFactory.getLogger(WaybillRouteLinkQueryManagerImpl.class);

    @Autowired
    private WaybillRouteLinkQueryAPI waybillRouteLinkQueryAPI;

    @Override
    @JProfiler(jKey = "com.jd.bluedragon.core.base.WaybillRouteLinkQueryManagerImpl.queryCustomWaybillRouteLink",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<WaybillRouteLinkResp> queryCustomWaybillRouteLink(String waybillCode) {
        log.info("WaybillRouteLinkQueryManagerImpl-queryCustomWaybillRouteLink-获取路由信息入参-{}",waybillCode);
        try{
            CommonDto<List<WaybillRouteLinkResp>> response = waybillRouteLinkQueryAPI.queryCustomWaybillRouteLink(waybillCode);
            if(CommonDto.CODE_SUCCESS == response.getCode() && CollectionUtils.isNotEmpty(response.getData())){
                return response.getData();
            }
        }catch (Exception e){
            log.error("获取路由信息异常-{}",e.getMessage(),e);
        }
        return null;
    }
}
