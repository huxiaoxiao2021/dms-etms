package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dao.unload.JyBizTaskUnloadVehicleStageDao;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jim.cli.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JyBizTaskUnloadVehicleStageServiceImpl implements JyBizTaskUnloadVehicleStageService{
    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;
    @Autowired
    JyBizTaskUnloadVehicleStageDao jyBizTaskUnloadVehicleStageDao;

    @Override
    public String generateStageBizId(String unloadVehicleBizId)  {
        String bizKey = "unload_task_stage_" +unloadVehicleBizId;
        Integer bizNo = 0;
        if (!ObjectHelper.isNotNull(redisClientCache.get(bizKey))) {
            redisClientCache.set(bizKey, "0", 48 * 60, TimeUnit.MINUTES, false);
        }
        try {
            bizNo = redisClientCache.incr(bizKey).intValue();
        } catch (Exception e) {
            return null;
        }
        return unloadVehicleBizId+"-"+bizNo;
    }

    @Override
    public JyBizTaskUnloadVehicleStageEntity queryCurrentStage(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.queryCurrentStage(condition);
    }

    @Override
    public int insertSelective(JyBizTaskUnloadVehicleStageEntity entityList) {
        return jyBizTaskUnloadVehicleStageDao.insertSelective(entityList);
    }

    @Override
    public int insertBatch(List<JyBizTaskUnloadVehicleStageEntity> entityList) {
        return jyBizTaskUnloadVehicleStageDao.insertBatch(entityList);
    }

    @Override
    public int updateByPrimaryKeySelective(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.updateByPrimaryKeySelective(condition);
    }

    @Override
    public int updateStatusByUnloadVehicleBizId(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.updateStatusByUnloadVehicleBizId(condition);
    }

    @Override
    public int updateStatusByBizId(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.updateStatusByBizId(condition);
    }

    @Override
    public List<Long> countByUnloadVehicleBizId(String unloadVehicleBizId) {
        return jyBizTaskUnloadVehicleStageDao.countByUnloadVehicleBizId(unloadVehicleBizId);
    }
}
