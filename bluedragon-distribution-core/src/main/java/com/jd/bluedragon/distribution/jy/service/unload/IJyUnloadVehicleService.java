package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

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
     * 拦截单号明细
     * @param request
     * @return
     */
    InvokeResult<InterceptScanBarCode> interceptBarCodeDetail(UnloadCommonRequest request);

    /**
     * 多扫单号明细
     * @param request
     * @return
     */
    InvokeResult<MoreScanBarCode> moreScanBarCodeDetail(UnloadCommonRequest request);

    /**
     * 卸车完成前预览是否有异常数据
     * @param request
     * @return
     */
    InvokeResult<UnloadPreviewData> unloadPreviewDashboard(UnloadCommonRequest request);

    /**
     * 卸车完成
     * @param request
     * @return
     */
    InvokeResult<Boolean> submitUnloadCompletion(UnloadCompleteRequest request);

    /**
     * 刷新卸车任务进度缓存
     * @param bizId
     * @return
     */
    Boolean refreshUnloadAggCache(String bizId);

    /**
     * 创建卸车任务
     * @param dto
     * @return
     */
    JyBizTaskUnloadDto createUnloadTask(JyBizTaskUnloadDto dto);
    /**
     * 查询待解封车数量
     * @param request
     * @return
     */
	InvokeResult<Long> countByVehicleNumberAndStatus(UnsealVehicleTaskRequest request);

    boolean judgeUnloadTaskNormal(UnloadPreviewData previewData, List<JyUnloadAggsEntity> unloadAggList);

}
