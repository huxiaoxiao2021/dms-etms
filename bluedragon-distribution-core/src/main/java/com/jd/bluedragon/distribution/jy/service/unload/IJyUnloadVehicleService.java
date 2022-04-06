package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
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
     * 查询待扫包裹
     * @param request
     * @return
     */
    InvokeResult<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request);

    /**
     * 创建卸车任务
     * @param entity
     * @return
     */
    boolean createUnloadTask(JyBizTaskUnloadVehicleEntity entity);
}
