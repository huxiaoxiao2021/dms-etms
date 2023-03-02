package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillRepaireApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.WaybillRegionDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/4
 * @Description:
 */
@Service("waybillRepaireManager")
public class WaybillRepaireManagerImpl implements WaybillRepaireManager{

    private Logger logger = LoggerFactory.getLogger(WaybillRepaireManagerImpl.class);

    @Autowired
    @Qualifier("waybillRepaireApi")
    private WaybillRepaireApi waybillRepaireApi;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillRepaireApi.getPackageCodeByOrgId",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<WaybillRegionDto> getPackageCodeByOrgId(WaybillRegionDto waybillRegionDto) {
        try{

            return waybillRepaireApi.getPackageCodeByOrgId(waybillRegionDto);

        }catch (Exception e){
            logger.error("getPackageCodeByOrgId error! {} ", JsonHelper.toJson(waybillRegionDto),e);
        }
        return null;
    }
}
