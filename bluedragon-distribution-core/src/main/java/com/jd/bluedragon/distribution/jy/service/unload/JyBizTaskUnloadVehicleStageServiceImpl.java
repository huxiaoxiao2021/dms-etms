package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.unload.JyBizTaskUnloadVehicleStageDao;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.generateStageBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.queryCurrentStage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleStageEntity queryCurrentStage(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.queryCurrentStage(condition);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.insertSelective", mState = {JProEnum.TP, JProEnum.FunctionError})
    public int insertSelective(JyBizTaskUnloadVehicleStageEntity entityList) {
        return jyBizTaskUnloadVehicleStageDao.insertSelective(entityList);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.insertBatch", mState = {JProEnum.TP, JProEnum.FunctionError})
    public int insertBatch(List<JyBizTaskUnloadVehicleStageEntity> entityList) {
        return jyBizTaskUnloadVehicleStageDao.insertBatch(entityList);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.updateByPrimaryKeySelective", mState = {JProEnum.TP, JProEnum.FunctionError})
    public int updateByPrimaryKeySelective(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.updateByPrimaryKeySelective(condition);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.updateStatusByUnloadVehicleBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public int updateStatusByUnloadVehicleBizId(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.updateStatusByUnloadVehicleBizId(condition);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.updateStatusByBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public int updateStatusByBizId(JyBizTaskUnloadVehicleStageEntity condition) {
        return jyBizTaskUnloadVehicleStageDao.updateStatusByBizId(condition);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.countByUnloadVehicleBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<Long> countByUnloadVehicleBizId(String unloadVehicleBizId) {
        return jyBizTaskUnloadVehicleStageDao.countByUnloadVehicleBizId(unloadVehicleBizId);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.selectUnloadDoingStageTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleStageEntity selectUnloadDoingStageTask(String bizId) {
        return jyBizTaskUnloadVehicleStageDao.selectUnloadDoingStageTask(bizId);
    }
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.getTaskCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public int getTaskCount(JyBizTaskUnloadVehicleStageEntity entity) {
        return jyBizTaskUnloadVehicleStageDao.getTaskCount(entity);
    }
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyBizTaskUnloadVehicleStageServiceImpl.queryByBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleStageEntity queryByBizId(String bizId) {
        return jyBizTaskUnloadVehicleStageDao.queryByBizId(bizId);
    }
}
