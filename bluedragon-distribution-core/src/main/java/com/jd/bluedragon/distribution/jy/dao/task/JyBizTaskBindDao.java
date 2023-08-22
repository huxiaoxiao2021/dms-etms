package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskBindEntityQueryCondition;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:05
 * @Description
 */
public class JyBizTaskBindDao extends BaseDao<JyBizTaskBindEntity> {

    private final static String NAMESPACE = JyBizTaskSendVehicleDao.class.getName();


    public List<JyBizTaskBindEntity> queryBindTaskByBindDetailBizIds(JyBizTaskBindEntityQueryCondition queryCondition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryBindTaskByBindDetailBizIds", queryCondition);

    }

    public int batchAdd(List<JyBizTaskBindEntity> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchAdd", entityList);
    }

    public int taskUnbinding(JyBizTaskBindEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".taskUnbinding", entity);
    }

    public List<JyBizTaskBindEntity> queryBindTaskList(JyBizTaskBindEntityQueryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryBindTaskList", condition);
    }


//    public int deleteByPrimaryKey(JyBizTaskBindEntity request){
//        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", request);
//    }
//
//    public int insert(JyBizTaskBindEntity request){
//        return this.getSqlSession().insert(NAMESPACE + ".insert", request);
//    }
//
//    public int insertSelective(JyBizTaskBindEntity request){
//        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", request);
//    }
//
//    public JyBizTaskBindEntity selectByPrimaryKey(Long id){
//        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
//    }
//
//    public int updateByPrimaryKeySelective(JyBizTaskBindEntity request){
//        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", request);
//    }
//
//    public int updateByPrimaryKey(JyBizTaskBindEntity request){
//        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", request);
//    }


}
