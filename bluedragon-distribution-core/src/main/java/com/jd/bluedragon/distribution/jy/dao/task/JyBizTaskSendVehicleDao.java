package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizSendTaskAssociationDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendLineTypeCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.enums.JySendTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.ObjectHelper;
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

    /**
     * 待发货、发货中、待封车、已封车四个状态采用不同计划发车时间范围的任务数量统计查询语句：目前接货仓有这种特殊需求在使用
     */
    public List<JyBizTaskSendCountDto> sumSpecifyTaskByVehicleStatus(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
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
        return this.getSqlSession().selectList(NAMESPACE + ".sumSpecifyTaskByVehicleStatus", params);
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
        if(sendVehicleBizList != null && sendVehicleBizList.size() > 0){
            params.put("sendVehicleBizList", sendVehicleBizList.toArray());
        }
        if (entity.getLineType() != null) {
            List<Integer> lineType = new ArrayList<>();
            if (JyLineTypeEnum.TRUNK_LINE.getCode().equals(entity.getLineType())
                ||JyLineTypeEnum.BRANCH_LINE.getCode().equals(entity.getLineType())){
                lineType.add(JyLineTypeEnum.OTHER.getCode());
            }
            lineType.add(entity.getLineType());
            params.put("lineTypeList", lineType.toArray());
        }
        if (typeEnum != null) {
            params.put("sortType", typeEnum.getCode());
        }
        if (statuses != null && statuses.size() > 0) {
            params.put("statuses", statuses.toArray());
        }
        params.put("offset", offset);
        params.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".querySendTaskOfPage", params);
    }

    public List<JyBizTaskSendVehicleEntity> querySpecifySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                                List<String> sendVehicleBizList,
                                                                JyBizTaskSendSortTypeEnum typeEnum,
                                                                Integer offset, Integer limit,
                                                                List<Integer> statuses) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        if(sendVehicleBizList != null && sendVehicleBizList.size() > 0){
            params.put("sendVehicleBizList", sendVehicleBizList.toArray());
        }
        if (entity.getLineType() != null) {
            List<Integer> lineType = new ArrayList<>();
            if (JyLineTypeEnum.TRUNK_LINE.getCode().equals(entity.getLineType())
                    ||JyLineTypeEnum.BRANCH_LINE.getCode().equals(entity.getLineType())){
                lineType.add(JyLineTypeEnum.OTHER.getCode());
            }
            lineType.add(entity.getLineType());
            params.put("lineTypeList", lineType.toArray());
        }
        if (typeEnum != null) {
            params.put("sortType", typeEnum.getCode());
        }
        if (statuses != null && statuses.size() > 0) {
            params.put("statuses", statuses.toArray());
        }
        params.put("offset", offset);
        params.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".querySpecifySendTaskOfPage", params);
    }

    public Integer countByCondition(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList, List<Integer> statuses) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        if (entity.getLineType() != null) {
            List<Integer> lineType = new ArrayList<>();
            if (JyLineTypeEnum.TRUNK_LINE.getCode().equals(entity.getLineType())
                    ||JyLineTypeEnum.BRANCH_LINE.getCode().equals(entity.getLineType())){
                lineType.add(JyLineTypeEnum.OTHER.getCode());
            }
            lineType.add(entity.getLineType());
            params.put("lineTypeList", lineType.toArray());
        }
        if(sendVehicleBizList != null && sendVehicleBizList.size() > 0){
            params.put("sendVehicleBizList", sendVehicleBizList.toArray());
        }
        if (statuses != null && statuses.size() > 0) {
            params.put("statuses", statuses.toArray());
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

    /**
     * 待发货、发货中、待封车三个状态采用不同计划发车时间范围的任务列表查询语句：目前接货仓有这种特殊需求在使用
     */
    public List<JyBizTaskSendVehicleEntity> findSpecifySendTaskByDestOfPage(JyBizTaskSendVehicleDetailEntity entity,
                                                                     Integer offset, Integer limit) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("offset", offset);
        params.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".findSpecifySendTaskByDestOfPage", params);
    }

    public Integer countSendTaskByDest(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countSendTaskByDest", entity);
    }

    public Integer countSpecifySendTaskByDest(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countSpecifySendTaskByDest", entity);
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

    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatusForTransfer(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        if (ObjectHelper.isNotNull(entity.getLineType())){
            List<Integer> lineType = new ArrayList<>();
            lineType.add(entity.getLineType());
            params.put("lineTypeList", lineType);
        }
        if (CollectionUtils.isNotEmpty(sendVehicleBizList)) {
            params.put("sendVehicleBizList", sendVehicleBizList);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".sumTaskByVehicleStatus", params);
    }

    public List<JyBizTaskSendVehicleEntity> findByTransWork(JyBizTaskSendVehicleEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".findByTransWork", entity);
    }
    
    public List<JyBizTaskSendVehicleEntity> findSendTaskByBizIds(List<String> bizIds) {
        Map<String,Object> params = new HashMap<>();
        params.put("bizIds", bizIds);
        return this.getSqlSession().selectList(NAMESPACE + ".findSendTaskByBizIds", params);
    }

    public List<JyBizTaskSendVehicleEntity> findSendTaskByDestAndStatusesWithPage(JyBizTaskSendVehicleDetailQueryEntity entity, List<Integer> statuses,
                                                                                  Integer offset, Integer limit) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("offset", offset);
        params.put("limit", limit);
        if (statuses != null && !statuses.isEmpty()) {
            params.put("statuses", statuses.toArray());
        }
        return this.getSqlSession().selectList(NAMESPACE + ".findSendTaskByDestAndStatusesWithPage", params);
    }

    public JyBizTaskSendVehicleEntity findByBookingCode(String bookingCode, Long startSiteId, Boolean ignoreYn) {
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBookingCode(bookingCode);
        entity.setStartSiteId(startSiteId);
        entity.setTaskType(JySendTaskTypeEnum.AVIATION.getCode());
        if(!Boolean.TRUE.equals(ignoreYn)) {
            entity.setYn(Constants.YN_YES);
        }
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBookingCode", entity);
    }


    public Integer countDetailSendTaskByCondition(JyBizTaskSendVehicleDetailEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countDetailSendTaskByCondition", entity);
    }
    public  List<JyBizSendTaskAssociationDto>  pageFindDetailSendTaskByCondition(JyBizTaskSendVehicleDetailQueryEntity entity, Integer offset, Integer limit) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity", entity);
        params.put("offset", offset);
        params.put("limit", limit);
        return this.getSqlSession().selectList(NAMESPACE + ".pageFindDetailSendTaskByCondition", params);
    }

    public List<String> findSpecifyStatusManualTaskByStayOverTime(JyBizTaskSendVehicleDetailQueryEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".findSpecifyStatusManualTaskByStayOverTime", entity);
    }

    public int batchUpdateByBizIds(JyBizTaskSendVehicleDetailQueryEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateByBizIds", entity);
    }

}
