package com.jd.bluedragon.distribution.jy.service.summary.impl;

import com.jd.bluedragon.distribution.jy.dao.summary.JySealStatisticsSummaryDao;
import com.jd.bluedragon.distribution.jy.service.summary.JySealStatisticsSummaryService;
import com.jd.bluedragon.distribution.jy.summary.JySealStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JySealStatisticsSummaryEntity;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/18 11:11
 * todo 闭环结果暂存，仅支持insert  select
 * @Description
 */
@Slf4j
@Service
public class JySealStatisticsSummaryServiceImpl implements JySealStatisticsSummaryService {


    @Autowired
    private JySealStatisticsSummaryDao statisticsSummaryDao;

    @Override
    public int insertSelective(JySealStatisticsSummaryEntity entity) {
        if(log.isInfoEnabled()) {
            log.info("汇总表保存entity={}", JsonHelper.toJson(entity));
        }
        return statisticsSummaryDao.insertSelective(entity);
    }

    @Override
    public List<JySealStatisticsSummaryEntity> queryByBusinessKeysAndType(JySealStatisticsSummaryCondition summaryCondition) {
        return statisticsSummaryDao.queryByBusinessKeysAndType(summaryCondition);
    }

    @Override
    public JySealStatisticsSummaryEntity queryByBusinessKeyAndType(JySealStatisticsSummaryEntity entity) {
        return statisticsSummaryDao.queryByBusinessKeyAndType(entity);
    }
}
