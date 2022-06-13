package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发车任务明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleDetailDao extends BaseDao<JyBizTaskSendVehicleDetailEntity> {

    private final static String NAMESPACE = JyBizTaskSendVehicleDetailDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public JyBizTaskSendVehicleDetailEntity findByBizId(String bizId){
        return this.getSqlSession().selectOne(NAMESPACE + ".select", bizId);
    }

    public int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateDateilTaskByVehicleBizId", entity);
    }

    public Long findByTransWorkItem(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByTransWorkItem", entity);
    }

    public int save(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".save", entity);
    }

    public List<JyBizTaskSendVehicleDetailEntity> findByMainVehicleBiz(JyBizTaskSendVehicleDetailEntity entity, List<Integer> statuses) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        if (CollectionUtils.isNotEmpty(statuses)) {
            params.put("statuses", statuses);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".findByMainVehicleBiz", params);
    }

    public int updateByBiz(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBiz", entity);
    }

    public int updateStatus(JyBizTaskSendVehicleDetailEntity entity, Integer oldStatus) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("oldStatus", oldStatus);
        return this.getSqlSession().update(NAMESPACE + ".updateStatus", params);
    }

    public int countByStatus(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countByStatus", entity);
    }

    public List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".getAllSendDest", entity);
    }
}
