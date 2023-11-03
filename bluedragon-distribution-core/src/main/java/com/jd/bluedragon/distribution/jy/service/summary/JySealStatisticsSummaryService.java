package com.jd.bluedragon.distribution.jy.service.summary;

import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryEntity;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/18 11:10
 * @Description
 */
public interface JySealStatisticsSummaryService {

    int insertSelective(JyStatisticsSummaryEntity entity) ;

    List<JyStatisticsSummaryEntity> queryByBusinessKeysAndType(JyStatisticsSummaryCondition summaryCondition);

    JyStatisticsSummaryEntity queryByBusinessKeyAndType(JyStatisticsSummaryEntity entity);
}
