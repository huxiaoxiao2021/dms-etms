package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.*;

import java.util.List;

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
    InvokeResult<List<UnloadVehicleTaskDto>> queryUnloadVehicleTaskByVehicleNumOrPackage(QueryUnloadTaskDto queryUnloadTaskDto);

    /**
     * 变更任务属性
     */
    InvokeResult updateUnloadVehicleTaskProper(UnloadVehicleTaskDto unloadVehicleTask);


    /**
     * 统计数据维度查询(按包裹 、运单 、板、任务查询统计数据（已扫 应扫 待扫 多扫 拦截）)
     *
     * @param dto
     * @return
     */
    InvokeResult<UnloadVehicleTaskDto> queryStatisticsByDiffDimension(DimensionQueryDto dto);

    /**
     * 查询卸车任务详情
     *
     * @param bizId
     * @return
     */
    InvokeResult<UnloadVehicleTaskDto> queryStatisticsDetailByDiffDimension(String bizId);


    /**
     * 扫描组板
     *
     * @param scanPackageDto
     * @return
     */
    InvokeResult<ScanPackageRespDto> scanAndComBoard(ScanPackageDto scanPackageDto);


    /**
     * 根据板号 查询组板基础详情
     * @param boardCode
     * @return
     */
    InvokeResult<ScanPackageRespDto> queryComBoardDataByBoardCode(String boardCode);


    /**
     * 查询下钻运单/包裹明细（按不同维度）
     *
     * @param queryUnloadDetailDto
     * @return
     */
    InvokeResult<List<UnloadWaybillDto>> queryUnloadDetailByDiffDimension(QueryUnloadDetailDto queryUnloadDetailDto);


    /**
     * 查询卸车任务下流向信息
     *
     * @param bizId
     * @return
     */
    InvokeResult<List<UnloadTaskFlowDto>> queryUnloadTaskFlow(String bizId);


    /**
     * 根据任务流向信息 查询组板信息
     * @param taskFlowDto
     * @return
     */
    InvokeResult<List<ComBoardDto>> queryComBoarUnderTaskFlow(TaskFlowDto taskFlowDto);


    /**
     * 根据板号查询组板下的运单信息
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
     * 查询货物分类信息（按不同维度查询： 按任务 按板）
     * @param queryGoodsCategory
     * @return
     */
    InvokeResult<List<GoodsCategoryDto>> queryGoodsCategoryByDiffDimension(QueryGoodsCategory queryGoodsCategory);

    /**
     * 取消组板
     * @param cancelComBoardDto
     * @return
     */
    InvokeResult cancelComBoard(CancelComBoardDto cancelComBoardDto);



}
