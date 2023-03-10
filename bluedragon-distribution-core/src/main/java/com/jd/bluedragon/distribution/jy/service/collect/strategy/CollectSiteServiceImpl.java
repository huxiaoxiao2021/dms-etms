package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //场地集齐服务
 * @date
 **/
@Service("collectSiteServiceImpl")
public class CollectSiteServiceImpl implements CollectStatisticsDimensionService, InitializingBean {


    @Override
    public CollectReportStatisticsDto collectStatistics(CollectStatisticsQueryParamDto paramDto) {
        //todo zcf
        return null;
    }

    @Override
    public List<CollectReportDto> queryCollectListPage(CollectReportReqDto collectReportReqDto) {
        //todo zcf   处理按条件查询单条运单统计信息
        return null;
    }

    @Override
    public List<CollectReportDetailPackageDto> queryCollectDetail(CollectReportReqDto collectReportReqDto) {
        //todo zcf
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectStatisticsDimensionFactory.registerCollectStatisticsDimension(CollectTypeEnum.SITE_JIQI.getCode(), this);
    }
}
