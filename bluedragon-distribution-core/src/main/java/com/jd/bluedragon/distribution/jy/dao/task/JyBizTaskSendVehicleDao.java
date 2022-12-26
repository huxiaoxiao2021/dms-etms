package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendLineTypeCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
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

    /**
     * 更新到来时间或者即将到来时间，取最小值为准更新
     * @param entity
     * @return
     */
    public int updateComeTimeOrNearComeTime(JyBizTaskSendVehicleEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateComeTimeOrNearComeTime", entity);
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
        if (entity.getLineType() != null) {
            List<Integer> lineType = new ArrayList<>();
            lineType.add(JyLineTypeEnum.OTHER.getCode());
            lineType.add(entity.getLineType());
            params.put("lineTypeList", lineType);
        }
        if (CollectionUtils.isNotEmpty(sendVehicleBizList)) {
            params.put("sendVehicleBizList", sendVehicleBizList);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".sumTaskByVehicleStatus", params);
    }

    public List<JyBizTaskSendLineTypeCountDto> sumTaskByLineType(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        if (CollectionUtils.isNotEmpty(sendVehicleBizList)) {
            params.put("sendVehicleBizList", sendVehicleBizList);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".sumTaskByLineType", params);
    }

    public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                                List<String> sendVehicleBizList,
                                                                JyBizTaskSendSortTypeEnum typeEnum,
                                                                Integer offset, Integer limit,
                                                                List<Integer> statuses) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("sendVehicleBizList", sendVehicleBizList);
        if (entity.getLineType() != null) {
            List<Integer> lineType = new ArrayList<>();
            lineType.add(JyLineTypeEnum.OTHER.getCode());
            lineType.add(entity.getLineType());
            params.put("lineTypeList", lineType);
        }
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

    public Integer countByCondition(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList, List<Integer> statuses) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("sendVehicleBizList", sendVehicleBizList);
        if (statuses != null && statuses.size() > 0) {
            params.put("statuses", statuses);
        }
        return this.getSqlSession().selectOne(NAMESPACE + ".countByCondition", params);
    }

    public int updateStatus(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("oldStatus", oldStatus);
        return this.getSqlSession().update(NAMESPACE + ".updateStatus", params);
    }

    public int updateStatusWithoutCompare(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("oldStatus", oldStatus);
        return this.getSqlSession().update(NAMESPACE + ".updateStatusWithoutCompare", params);
    }

    public int updateBizTaskSendStatus(JyBizTaskSendVehicleEntity toSvTask) {
        return this.getSqlSession().update(NAMESPACE + ".updateBizTaskSendStatus", toSvTask);
    }

    public List<JyBizTaskSendVehicleEntity> findSendTaskByDestOfPage(JyBizTaskSendVehicleDetailEntity entity,
                                                                     Integer offset, Integer limit) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("offset", offset);
        params.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".findSendTaskByDestOfPage", params);
    }

    public Integer countSendTaskByDest(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countSendTaskByDest", entity);
    }

    public List<JyBizTaskSendVehicleEntity> findSendTaskByTransWorkCode(List<String> transWorkCodeList,Long startSiteId) {
        Map<String,Object> params = new HashMap<>();
        params.put("transWorkCodeList", transWorkCodeList);
        params.put("startSiteId", startSiteId);
        return this.getSqlSession().selectList(NAMESPACE + ".findSendTaskByTransWorkCode", params);
    }

	public int countBizNumForCheckLineType(JyBizTaskSendVehicleEntity checkQuery, List<String> bizIdList,
			List<Integer> lineTypes) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", checkQuery);
        params.put("sendVehicleBizList", bizIdList);
        params.put("lineTypeList", lineTypes);
        return this.getSqlSession().selectOne(NAMESPACE + ".countBizNumForCheckLineType", params);
	}
}
