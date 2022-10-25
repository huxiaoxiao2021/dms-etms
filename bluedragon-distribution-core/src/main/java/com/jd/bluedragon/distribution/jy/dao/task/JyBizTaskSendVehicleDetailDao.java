package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
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
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateDateilTaskByVehicleBizId", entity);
    }

    public Long findByCondition(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByCondition", entity);
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

    public int updateStatusWithoutCompare(JyBizTaskSendVehicleDetailEntity entity, Integer oldStatus) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("oldStatus", oldStatus);
        return this.getSqlSession().update(NAMESPACE + ".updateStatusWithoutCompare", params);
    }

    public Integer countByCondition(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countByCondition", entity);
    }

    public List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".getAllSendDest", entity);
    }

    public JyBizTaskSendVehicleDetailEntity findSendDetail(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findSendDetail", entity);
    }

    public List<JyBizTaskSendCountDto> sumByVehicleStatus(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".sumByVehicleStatus", entity);
    }

    public int updateBizTaskSendDetailStatus(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateBizTaskSendDetailStatus", entity);
    }

    public Integer countNoCancelSendDetail(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countNoCancelSendDetail", entity);
    }

	public List<String> findSendVehicleBizListBySendFlow(JyBizTaskSendVehicleDetailEntity entity) {
		return this.getSqlSession().selectList(NAMESPACE + ".findSendVehicleBizListBySendFlow", entity);
	}

	public JyBizTaskSendVehicleDetailEntity findByTransWorkItemCode(JyBizTaskSendVehicleDetailEntity query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".findByTransWorkItemCode", query);
	}

    public JyBizTaskSendVehicleDetailEntity queryByTransWorkItemCode(JyBizTaskSendVehicleDetailEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByTransWorkItemCode", query);
    }
}
