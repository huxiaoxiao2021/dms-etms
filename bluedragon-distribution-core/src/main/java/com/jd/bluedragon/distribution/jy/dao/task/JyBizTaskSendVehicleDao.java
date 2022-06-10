package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发车业务任务表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleDao extends BaseDao<JyBizTaskSendVehicleEntity> {

    private final static String NAMESPACE = JyBizTaskSendVehicleDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    /**
     * 根据bizID查询任务详情
     * @param bizId
     * @return
     */
    public JyBizTaskSendVehicleEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public int updateByBizId(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }

    public JyBizTaskSendVehicleEntity findByTransWorkAndStartSite(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByTransWorkAndStartSite", entity);
    }

    public int initTaskSendVehicle(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".initTaskSendVehicle", entity);
    }

    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatus(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        if (CollectionUtils.isNotEmpty(sendVehicleBizList)) {
            params.put("sendVehicleBizList", sendVehicleBizList);
        }
        return this.getSqlSession().selectList(NAMESPACE + "sumTaskByVehicleStatus", params);
    }

    public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                                List<String> sendVehicleBizList,
                                                                JyBizTaskSendSortTypeEnum typeEnum,
                                                                Integer offset, Integer limit,
                                                                List<Integer> statuses) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("sendVehicleBizList", sendVehicleBizList);
        if (typeEnum != null) {
            params.put("sortType", typeEnum.getCode());
        }
        if (statuses != null && statuses.size() > 0) {
            params.put("statuses", statuses);
        }
        params.put("offset", offset);
        params.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".querySendTaskOfPage", params);
    }

    public int updateStatus(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("oldStatus", oldStatus);
        return this.getSqlSession().update(NAMESPACE + ".updateStatus", params);
    }

}
