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
     * @return
     */
    InvokeResult<List<UnloadVehicleTaskDto>>  queryUnloadVehicleTaskByVehicleNumOrPackage(QueryUnloadTaskDto queryUnloadTaskDto);

    /**
     * 变更任务属性
     */
    InvokeResult updateUnloadVehicleTaskProper(UnloadVehicleTaskDto unloadVehicleTask);


    /**
     * 统计数据维度查询(按包裹 、运单 、板、任务查询统计数据（已扫 应扫 待扫 多扫 拦截）)
     * @param dto
     * @return
     */
    InvokeResult<UnloadVehicleTaskDto> queryStatisticsByDiffDimension(DimensionQueryDto dto);

    /**
     * 查询卸车任务详情
     * @param bizId
     * @return
     */
    InvokeResult<UnloadVehicleTaskDto> queryStatisticsDetailByDiffDimension(String bizId);
}
