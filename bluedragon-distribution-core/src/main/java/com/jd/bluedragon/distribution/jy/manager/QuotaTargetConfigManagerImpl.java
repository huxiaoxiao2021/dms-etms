package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.common.annotation.CacheMethod;
import com.jd.dms.wb.sdk.api.loss.IQuotaTargetConfigJsfService;
import com.jd.dms.wb.sdk.dto.loss.QuotaTargetConfigDto;
import com.jd.dms.wb.sdk.dto.loss.QuotaTargetConfigRequest;
import com.jd.dms.wb.sdk.enums.oneTable.TimeTypeEnum;
import com.jd.dms.wb.sdk.model.base.BaseEntity;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class QuotaTargetConfigManagerImpl implements IQuotaTargetConfigManager{
    @Autowired(required = false)
    private IQuotaTargetConfigJsfService quotaTargetConfigJsfService;

    /**
     * 丢失一表通-发货扫描-目标
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.QuotaTargetConfigManagerImpl.getLostOneTableSendScanTarget",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @CacheMethod(key="IQuotaTargetConfigManager.getLostOneTableSendScanTarget-{0}-{1}-{2}-{3}", cacheBean="redisCache", 
            timeout = 1000 * 60 * 30)
    public Double getLostOneTableSendScanTarget(String quotaCode, String year, String businessType, String class2Type){
        QuotaTargetConfigRequest request = new QuotaTargetConfigRequest();
        request.setBusinessType(businessType);
        request.setQuotaCode(quotaCode);
        request.setYear(year);
        request.setClass2Type(class2Type);
        BaseEntity<List<QuotaTargetConfigDto>> baseEntity =  quotaTargetConfigJsfService.queryByCondition(request);
        if(baseEntity != null && !CollectionUtils.isEmpty(baseEntity.getData())){
            for(QuotaTargetConfigDto dto : baseEntity.getData()){
                if(TimeTypeEnum.DAY.getCode().equals(dto.getTimeType()) && dto.getTarget() != null){
                    log.info("获取成功-丢失一表通-发货扫描-目标值:{},BusinessType:{},QuotaCode:{},year:{},Class2Type:{}",
                            dto.getTarget(),request.getBusinessType(),request.getQuotaCode(), request.getYear(),
                            request.getClass2Type());
                    return dto.getTarget();
                }
            }
        }
        log.info("未查到丢失一表通-发货扫描-目标值,BusinessType:{},QuotaCode:{},year:{},Class2Type:{}",
                request.getBusinessType(),request.getQuotaCode(), request.getYear(), request.getClass2Type());
        return null;
    }
}
