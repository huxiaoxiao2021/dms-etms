package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;

import java.util.List;

public class JyBizTaskUnloadVehicleStageDao extends BaseDao<JyBizTaskUnloadVehicleStageEntity> {
    final static String NAMESPACE = JyBizTaskUnloadVehicleStageDao.class.getName();

    public int insert(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int insertSelective(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyBizTaskUnloadVehicleStageEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", entity);
    }

    public int updateStatusByUnloadVehicleBizId(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateStatusByUnloadVehicleBizId", entity);
    }

    public int updateStatusByBizId(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateStatusByBizId", entity);
    }

    public int insertBatch(List<JyBizTaskUnloadVehicleStageEntity> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".insertBatch", entityList);
    }

    public JyBizTaskUnloadVehicleStageEntity queryCurrentStage(JyBizTaskUnloadVehicleStageEntity condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryCurrentStage", condition);
    }

    public List<JyBizTaskUnloadVehicleStageEntity> queryByParentBizId(String parentBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByParentBizId", parentBizId);
    }

    public List<Long> countByUnloadVehicleBizId(String unloadVehicleBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".countByUnloadVehicleBizId", unloadVehicleBizId);
    }

    public JyBizTaskUnloadVehicleStageEntity queryByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByBizId", bizId);
    }

    public JyBizTaskUnloadVehicleStageEntity selectUnloadDoingStageTask(String parentBizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectUnloadDoingStageTask", parentBizId);
    }

    public int getTaskCount(JyBizTaskUnloadVehicleStageEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".getTaskCount", entity);
    }
}
