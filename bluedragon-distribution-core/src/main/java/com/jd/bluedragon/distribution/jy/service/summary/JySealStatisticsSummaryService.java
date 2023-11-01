package com.jd.bluedragon.distribution.jy.service.summary;

import com.jd.bluedragon.distribution.jy.summary.JySealStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JySealStatisticsSummaryEntity;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/18 11:10
 * @Description
 */
public interface JySealStatisticsSummaryService {

    int insertSelective(JySealStatisticsSummaryEntity entity) ;

    List<JySealStatisticsSummaryEntity> queryByBusinessKeysAndType(JySealStatisticsSummaryCondition summaryCondition);

    JySealStatisticsSummaryEntity queryByBusinessKeyAndType(JySealStatisticsSummaryEntity entity);
}
