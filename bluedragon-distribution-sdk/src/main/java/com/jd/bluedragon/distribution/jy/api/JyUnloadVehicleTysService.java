package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.*;

import java.util.List;

/**
 * @author weixiaofeng12
 * @date 2022-07-01
 * 转运卸车岗相关服务
 */
public interface JyUnloadVehicleTysService {
    /**
     * 查询转运卸车任务列表-分页查询
     */
    InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto);

    /**
     * 根据车牌号或者包裹号检索任务信息
     *
     * @return
     */
    InvokeResult<UnloadVehicleTaskRespDto> queryUnloadVehicleTaskByVehicleNumOrPackage(UnloadVehicleTaskReqDto queryUnloadTaskDto);

    /**
     * 变更任务属性
     */
    InvokeResult updateUnloadVehicleTaskProperty(UnloadVehicleTaskDto unloadVehicleTask);


    /**
     * 统计数据维度查询(按任务、板查询统计数据（已扫 应扫 待扫 多扫 拦截）)
     *
     * @param dto
     * @return
     */
    InvokeResult<ScanStatisticsDto> queryStatisticsByDiffDimension(DimensionQueryDto dto);

    /**
     * 查询包裹和运单维度统计数据
     */
    InvokeResult<StatisticsDto> queryStatistics(DimensionQueryDto dto);


    /**
     * 查询卸车任务详情
     *
     * @param bizId
     * @return
     */
    InvokeResult<UnloadVehicleTaskDto> queryTaskDataByBizId(String bizId);

    /**
     * 查询组板基础详情
     * @param boardCode
     * @return
     */
    InvokeResult<ComBoardDto> queryComBoardDataByBoardCode(String boardCode);

    /**
     * 交班
     * @param unloadVehicleTask
     * @return
     */
    InvokeResult<Void> handoverTask(UnloadVehicleTaskDto unloadVehicleTask);


    /**
     * 人工作业扫描（组板+扫描）
     *
     * @param scanPackageDto
     * @return
     */
    InvokeResult<ScanPackageRespDto> scan(ScanPackageDto scanPackageDto);


    /**
     * 流水线作业扫描（扫描）
     * @param scanPackageDto
     * @return
     */
    InvokeResult<ScanPackageRespDto> scanForPipelining (ScanPackageDto scanPackageDto);


    /**
     * 查询货物分类信息（按不同维度查询： 按任务 按板）
     * @param queryGoodsCategory
     * @return
     */
    InvokeResult<List<GoodsCategoryDto>> queryGoodsCategoryByDiffDimension(QueryGoodsCategory queryGoodsCategory);


    /**
     * 查询扫描（待扫 拦截 多扫）下钻运单明细
     *
     * @param queryUnloadDetailDto
     * @return
     */
    InvokeResult<ScanStatisticsInnerDto> queryUnloadDetailByDiffDimension(QueryUnloadDetailDto queryUnloadDetailDto);


    /**
     * 查询卸车任务下流向信息
     *
     * @param bizId
     * @return
     */
    InvokeResult<List<UnloadTaskFlowDto>> queryUnloadTaskFlow(String bizId);


    /**
     * 查询流向下组板信息
     * @param taskFlowDto
     * @return
     */
    InvokeResult<TaskFlowComBoardDto> queryComBoarUnderTaskFlow(TaskFlowDto taskFlowDto);


    /**
     * 根据板号查询组板下的运单信息 查板箱关系表
     * @param queryBoardDto
     * @return
     */
    InvokeResult<List<UnloadWaybillDto>>  queryWaybillUnderBoard(QueryBoardDto queryBoardDto);

    /**
     * 根据运单查询包裹明细
     * @param queryWaybillDto
     * @return
     */

    InvokeResult<List<UnloadPackageDto>>  queryPackageUnderWaybill(QueryWaybillDto queryWaybillDto);


    /**
     * 取消组板
     * @param cancelComBoardDto
     * @return
     */
    InvokeResult cancelComBoard(CancelComBoardDto cancelComBoardDto);

    /**
     * 组板完成
     * @param boardCode 板号
     * @return
     */
    InvokeResult<Void> comBoardComplete(String boardCode);

    /**
     * 查询路由信息
     * @param waybillCode
     * @param startSiteId
     * @return
     */
    Integer getWaybillNextRouter(String waybillCode, Integer startSiteId);

    /**
     * 查询货区编码
     * @param currentSiteCode
     * @param nextSiteCode
     * @return
     */
    String getGoodsAreaCode(Integer currentSiteCode, Integer nextSiteCode);


    /**
     * 查询主子任务信息
     * @param masterBizId  主任务Id
     * @param queryChildTaskFlag  true : 查子任务   false: 不传
     * @return
     */
    InvokeResult<UnloadMasterChildTaskRespDto> queryMasterChildTaskInfoByBizId(String masterBizId, Boolean queryChildTaskFlag);


    /**
     * 查询子任务信息
     * @param childBizId
     * @return
     */
    InvokeResult<UnloadChildTaskDto> queryChildTaskInfoByBizId(String childBizId);

    /**
     * 根据主任务bizId查询卸车任务板关系信息
     */
    InvokeResult<List<UnloadBoardRespDto>> queryTaskBoardInfoByBizId(String masterBizId);

    /**
     * 根据包裹号查询任务下流向及板信息
     * @param flowBoardDto
     * @return
     */
    InvokeResult<FlowBoardDto> getTaskFlowBoardInfoByPackageCode(FlowBoardDto flowBoardDto);


}
