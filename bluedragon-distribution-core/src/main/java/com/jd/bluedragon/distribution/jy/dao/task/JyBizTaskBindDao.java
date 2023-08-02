package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:05
 * @Description
 */
public class JyBizTaskBindDao extends BaseDao<JyBizTaskBindEntity> {

    private final static String NAMESPACE = JyBizTaskSendVehicleDao.class.getName();


    public int deleteByPrimaryKey(JyBizTaskBindEntity request){
        return this.getSqlSession().update(NAMESPACE + ".insert", request);
    }

    public int insert(JyBizTaskBindEntity request){
        return this.getSqlSession().insert(NAMESPACE + ".insert", request);
    }

    public int insertSelective(JyBizTaskBindEntity request){
        return this.getSqlSession().insert(NAMESPACE + ".insert", request);
    }

    public JyBizTaskBindEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".insert", id);
    }

    public int updateByPrimaryKeySelective(JyBizTaskBindEntity request){
        return this.getSqlSession().update(NAMESPACE + ".insert", request);
    }

    public int updateByPrimaryKey(JyBizTaskBindEntity request){
        return this.getSqlSession().update(NAMESPACE + ".insert", request);
    }
}
