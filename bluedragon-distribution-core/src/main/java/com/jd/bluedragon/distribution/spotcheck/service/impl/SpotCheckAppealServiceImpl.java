package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckAppealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 抽检申诉服务实现
 */
@Service("spotCheckAppealService")
public class SpotCheckAppealServiceImpl implements SpotCheckAppealService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckAppealServiceImpl.class);


    @Override
    public void insertRecord(SpotCheckAppealEntity spotCheckAppealEntity) {

    }

    @Override
    public List<SpotCheckAppealEntity> findByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
        return null;
    }

    @Override
    public Integer countByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
        return null;
    }

    @Override
    public void updateById(SpotCheckAppealEntity spotCheckAppealEntity) {

    }

    @Override
    public void batchUpdateByIds(SpotCheckAppealEntity spotCheckAppealEntity) {

    }
}
