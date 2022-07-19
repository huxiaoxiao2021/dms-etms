package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;

import java.util.List;

public class JyBizTaskUnloadVehicleStageDao extends BaseDao<JyBizTaskUnloadVehicleStageEntity> {
    final static String NAMESPACE = JyBizTaskUnloadVehicleStageDao.class.getName();

    int insert(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    int insertSelective(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    JyBizTaskUnloadVehicleStageEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    int updateByPrimaryKeySelective(JyBizTaskUnloadVehicleStageEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", entity);
    }

    public int insertBatch(List<JyBizTaskUnloadVehicleStageEntity> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".insertBatch", entityList);
    }

    public JyBizTaskUnloadVehicleStageEntity queryCurrentStage(JyBizTaskUnloadVehicleStageEntity condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryCurrentStage", condition);
    }
}
