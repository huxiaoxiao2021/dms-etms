package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;

public class JyUnloadVehicleBoardDao extends BaseDao<JyUnloadVehicleBoardEntity> {

    final static String NAMESPACE = JyUnloadVehicleBoardDao.class.getName();

    public int insertSelective(JyUnloadVehicleBoardEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyUnloadVehicleBoardEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyUnloadVehicleBoardEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", entity);
    }

}
