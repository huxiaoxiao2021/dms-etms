package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.distribution.spotcheck.dao.SpotCheckAppealDao;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckAppealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 抽检申诉服务实现
 */
@Service("spotCheckAppealService")
public class SpotCheckAppealServiceImpl implements SpotCheckAppealService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckAppealServiceImpl.class);

    @Autowired
    private SpotCheckAppealDao spotCheckAppealDao;


    @Override
    public void insertRecord(SpotCheckAppealEntity spotCheckAppealEntity) {
        spotCheckAppealDao.insertRecord(spotCheckAppealEntity);
    }

    @Override
    public List<SpotCheckAppealEntity> findByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.findByCondition(spotCheckAppealEntity);
    }

    @Override
    public Integer countByCondition(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.countByCondition(spotCheckAppealEntity);
    }

    @Override
    public void updateById(SpotCheckAppealEntity spotCheckAppealEntity) {
        spotCheckAppealDao.updateById(spotCheckAppealEntity);
    }

    @Override
    public void batchUpdateByIds(SpotCheckAppealEntity spotCheckAppealEntity) {
        spotCheckAppealDao.batchUpdateByIds(spotCheckAppealEntity);
    }

    @Override
    public SpotCheckAppealEntity findById(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.findById(spotCheckAppealEntity);
    }

    @Override
    public List<SpotCheckAppealEntity> batchFindByIds(SpotCheckAppealEntity spotCheckAppealEntity) {
        return spotCheckAppealDao.batchFindByIds(spotCheckAppealEntity);
    }
}
