package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

import java.util.Date;
import java.util.List;

public interface JyBizTaskSendVehicleDetailService {
    JyBizTaskSendVehicleDetailEntity findByBizId(String bizId);

    int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity detailEntity);

    int updateByBiz(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 保存发货流向
     * @param detailEntity
     * @return
     */
    int saveTaskSendDetail(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 根据条件查询发车任务明细
     * @param entity
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findEffectiveSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity);

    List<JyBizTaskSendVehicleDetailEntity> findSendVehicleDetailByTransWorkCode(JyBizTaskSendVehicleEntity entity);

    /**
     * 按条件查询未封车的流向任务
     * @param entity
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findNoSealCarSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 按状态查发货任务流向
     * @param entity
     * @param statuses
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findBySiteAndStatus(JyBizTaskSendVehicleDetailEntity entity, List<Integer> statuses);

    /**
     * 取消
     * @param detailEntity
     * @return
     */
    int cancelDetail(JyBizTaskSendVehicleDetailEntity detailEntity);

    int cancelDetailTaskAndMainTask(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 按顺序更新发货明细状态
     * @param detailEntity
     * @param oldStatus
     * @return
     */
    int updateStatus(JyBizTaskSendVehicleDetailEntity detailEntity, Integer oldStatus);

    /**
     * 不按顺序更新发货明细状态
     * @param detailEntity
     * @param oldStatus
     * @return
     */
    int updateStatusWithoutCompare(JyBizTaskSendVehicleDetailEntity detailEntity, Integer oldStatus);

    /**
     * 按状态统计流向数量
     * @param entity
     * @return
     */
    Integer countByCondition(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 查询发车任务的所有流向
     * @param entity
     * @return
     */
    List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 查询一条发货流向记录
     * @param entity
     * @return
     */
    JyBizTaskSendVehicleDetailEntity findSendDetail(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 按状态统计发货明细数量
     * @param entity
     * @return
     */
    List<JyBizTaskSendCountDto> sumByVehicleStatus(JyBizTaskSendVehicleDetailEntity entity);

    int updateBizTaskSendDetailStatus(JyBizTaskSendVehicleDetailEntity entity);

    Integer countNoCancelSendDetail(JyBizTaskSendVehicleDetailEntity entity);
    /**
     * 根据发货流向查询主任务id列表
     * @param entity
     * @return
     */
    List<String> findSendVehicleBizListBySendFlow(JyBizTaskSendVehicleDetailEntity entity);
    /**
     * 根据transWorkItemCode查询一条数据
     * @param query
     * @return
     */
	JyBizTaskSendVehicleDetailEntity findByTransWorkItemCode(JyBizTaskSendVehicleDetailEntity query);

    JyBizTaskSendVehicleDetailEntity findBySendVehicleBizId(String sendVehicleBizId);

    /**
     * 根据流向list查询发货明细表中主任务BizIds
     * @param queryEntity
     * @return
     */
    List<String> findBizIdsBySiteFlows(JyBizTaskSendVehicleDetailQueryEntity queryEntity);

    /**
     * 根据BizIds批量获取任务
     * @param siteCode
     * @param bizIds
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findSendVehicleDetailByBizIds(int siteCode, List<String> bizIds);

    /**
     * 根据send_vehicle_biz_id批量获取任务
     * @param sendVehicleBizIds
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findDetailBySendVehicleBizIds(List<String> sendVehicleBizIds);


    /**
     * 根据明细bizList获取任务明细信息
     * @param detailBizList
     * @param siteId
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findByDetailVehicleBiz(List<String> detailBizList, Integer siteId);

    /**
     * 根据bizId更新派车任务状态
     * @param detailBizList
     * @param status
     * @return
     */
    boolean updateStatusByDetailVehicleBizIds(List<String> detailBizList, Integer status);

    /**
     * 查询未封车bizId
     * @param bizIds
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findNoSealTaskByBizIds(List<String> bizIds);
}
