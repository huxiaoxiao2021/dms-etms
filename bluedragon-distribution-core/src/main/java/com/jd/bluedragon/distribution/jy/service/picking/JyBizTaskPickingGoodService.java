package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.AirRailTaskCountDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.*;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:13
 * @Description
 */
public interface JyBizTaskPickingGoodService {
    /**
     * 根据bizId待提货任务
     * [调用方自己考虑是否查询yn=0]
     * @param bizId
     * @param ignoreYn  true: 忽略yn   false or null：只查yn=1
     * @return
     */
    JyBizTaskPickingGoodEntity findByBizIdWithYn(String bizId, Boolean ignoreYn);

    /**
     * 自建提货任务生成
     * @return
     */
    JyBizTaskPickingGoodEntity generateManualCreateTask(CurrentOperate site, User user, Integer taskType);

    /**
     * 获取自建任务唯一bizId
     * @param isNoTaskFlag  true： 无任务bizId
     * @return
     */
    String genPickingGoodTaskBizId(Boolean isNoTaskFlag);

    /**
     * 根据bizId修改任务信息
     * @param entity
     * @return
     */
    int updateTaskByBizIdWithCondition(JyBizTaskPickingGoodEntityCondition entity);

    boolean updateStatusByBizId(String bizId, Integer status, User operator);

    /**
     * 根据场地查找最新的可发货自建任务[未完成]
     * @param siteId
     * @return
     */
    JyBizTaskPickingGoodEntity findLatestEffectiveManualCreateTask(Long siteId);

    /**
     * 根据空铁批货流水号查询最近提货任务
     * 注：空铁业务特点，一批货为一组，一组存在多个场地提货，生成多个提货任务，一组任务唯一个整体，存在都存在，删除都删除
     * @param businessNumber
     * @return
     */
    JyBizTaskPickingGoodEntity findLatestTaskByBusinessNumber(String businessNumber);

    /**
     * 根据空铁批货流水号查询所有提货任务
     * @param businessNumber
     * @return
     */
    List<JyBizTaskPickingGoodEntity> findAllTaskByBusinessNumber(String businessNumber);

    /**
     * 根据空铁批货流水号删除一批任务
     * @param businessNumber
     * @return
     */
    int deleteByBusinessNumber(String businessNumber);


    /**
     * 提货任务按机场编码分组逻辑分页查询
     * @param queryDto
     * @return
     */
    List<JyBizTaskPickingGoodEntity> listTaskGroupByPickingNodeCode(JyPickingTaskGroupQueryDto queryDto);

    /**
     * 根据机场/车站编码批量查询提货任务
     * @param queryDto
     * @return
     */
    List<JyBizTaskPickingGoodEntity> listTaskByPickingNodeCode(JyPickingTaskBatchQueryDto queryDto);

    /**
     * 统计各个状态的任务数
     * @param queryDto
     * @return
     */
    List<AirRailTaskCountDto> countAllStatusByPickingSiteId(AirRailTaskCountQueryDto queryDto);

    /**
     * 批量完成任务
     * @param updateDto
     * @return
     */
    int batchUpdateStatusByBizId(JyPickingTaskBatchUpdateDto updateDto);

    /**
     * 分页查询最近几天的自建任务bizId
     * @param queryDto
     * @return
     */
    List<String> pageRecentCreatedManualBizId(JyBizTaskPickingGoodQueryDto queryDto);

    /**
     * 批量插入任务主数据
     * @param taskEntityList
     */
    void batchInsertTask(List<JyBizTaskPickingGoodEntity> taskEntityList);
    /**
     * 批量插入任务附属信息
     * @param subsidiaryEntityList
     */
    void batchInsertTaskSubsidiary(List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList);

    /**
     * 根据流向批量查询发货任务
     * @param queryDto
     * @return
     */
    List<JyBizTaskPickingGoodEntity> listTaskByPickingSiteId(JyPickingTaskBatchQueryDto queryDto);

    /**
     * 查找bizIdList 中自建任务的bizId集合
     * @param bizIdList
     * @return
     */
    List<String> findManualCreateTaskBizIds(List<String> bizIdList);
}
