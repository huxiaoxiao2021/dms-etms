package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.jy.dto.collect.CollectQueryReqDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDetailPackageDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportStatisticsDto;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectStatisticsDimensionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //本车任务集齐
 * @date
 **/
@Service("collectTaskServiceImpl")
public class CollectTaskServiceImpl implements CollectStatisticsDimensionService, InitializingBean {


    @Override
    public List<CollectReportStatisticsDto> collectStatistics(CollectQueryReqDto collectQueryReqDto) {
        //todo zcf
        return null;
    }

    @Override
    public List<CollectReportDto> queryCollectListPage(CollectQueryReqDto collectQueryReqDto) {
        //todo zcf  处理按条件查询单条运单统计信息
        return null;
    }

    @Override
    public List<CollectReportDetailPackageDto> queryCollectDetail(CollectQueryReqDto collectQueryReqDto) {
        //todo zcf
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CollectStatisticsDimensionFactory.registerCollectStatisticsDimension(CollectTypeEnum.TASK_JIQI.getCode(), this);

    }
}
