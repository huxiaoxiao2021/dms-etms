package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanAggByProductType;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanDetail;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;

import java.util.List;

/**
 * @ClassName IUnloadVehicleService
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
public interface IJyUnloadVehicleService {

    /**
     * 拉取卸车任务
     * @param request
     * @return
     */
    InvokeResult<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request);

    /**
     * 卸车扫描
     * @param request
     * @return
     */
    InvokeResult<Integer> unloadScan(UnloadScanRequest request);

    /**
     * 查询卸车进度
     * @param request
     * @return
     */
    InvokeResult<UnloadScanDetail> unloadDetail(UnloadCommonRequest request);

    /**
     * 卸车货物明细
     * @param request
     * @return
     */
    InvokeResult<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadGoodsRequest request);

    /**
     * 按产品类型统计待扫包裹总数
     * @param request
     * @return
     */
    InvokeResult<List<ProductTypeAgg>> toScanAggByProduct(UnloadCommonRequest request);

    /**
     * 创建卸车任务
     * @param dto
     * @return
     */
    boolean createUnloadTask(JyBizTaskUnloadDto dto);

    /**
     * 卸车任务领取和分配
     * @param dto
     * @return
     */
    boolean drawUnloadTask(JyBizTaskUnloadDto dto);

    /**
     * 卸车任务完成
     * @param dto
     * @return
     */
    boolean completeUnloadTask(JyBizTaskUnloadDto dto);
}
