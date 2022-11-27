package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.transport.request.TransportTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.photo.GoodsPhotoInfoDto;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.dto.select.StringSelectOption;

import java.util.List;

/**
 * @ClassName JyUnloadVehicleGatewayService
 * @Description 拣运卸车岗网关服务
 * @Author wyh
 * @Date 2022/4/1 16:39
 **/
public interface JyUnloadVehicleGatewayService {

    /**
     * 创建无任务卸车
     *
     * @param request
     * @return
     */
    JdCResponse<UnloadNoTaskResponse> createNoTaskUnloadTask(UnloadNoTaskRequest request);

    /**
     * 卸车任务列表
     * @param request
     * @return
     */
    JdCResponse<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request);

    /**
     * 车辆状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> vehicleStatusOptions();

    /**
     * 产品类型枚举
     * @return
     */
    JdCResponse<List<StringSelectOption>> productTypeOptions();

    /**
     * 卸车扫描方式枚举
     * @return 扫描方式列表
     * @author fanggang7
     * @time 2022-07-05 23:20:20 周二
     */
    JdCResponse<List<SelectOption>> scanTypeOptions();

    /**
     * 卸车扫描
     * @param request
     * @return
     */
    JdVerifyResponse<Integer> unloadScan(UnloadScanRequest request);

    /**
     * 卸车明细
     * @param request
     * @return
     */
    JdCResponse<UnloadScanDetail> unloadDetail(UnloadCommonRequest request);

    /**
     * 货物明细
     * @param request
     * @return
     */
    JdCResponse<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadGoodsRequest request);

    /**
     * 按产品类型统计待扫包裹总数
     * @param request
     * @return
     */
    JdCResponse<List<ProductTypeAgg>> toScanAggByProduct(UnloadCommonRequest request);

    /**
     * 待扫单号明细
     * @param request
     * @return
     */
    JdCResponse<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request);

    /**
     * 拦截单号明细
     * @param request
     * @return
     */
    JdCResponse<InterceptScanBarCode> interceptBarCodeDetail(UnloadCommonRequest request);

    /**
     * 多扫单号明细
     * @param request
     * @return
     */
    JdCResponse<MoreScanBarCode> moreScanBarCodeDetail(UnloadCommonRequest request);

    /**
     * 卸车完成前预览是否有异常数据
     * @param request
     * @return
     */
    JdCResponse<UnloadPreviewData> unloadPreviewDashboard(UnloadCommonRequest request);

    /**
     * 卸车完成
     * @param request
     * @return
     */
    JdCResponse<Boolean> submitUnloadCompletion(UnloadCompleteRequest request);
    /**
     * 查询解封车任务数量
     * @param request
     * @return
     */
    JdCResponse<Long> countByVehicleNumberAndStatus(UnsealVehicleTaskRequest request);

    /**
     * 串点运输任务提示
     *
     * @param request
     * @return
     */
    JdVerifyResponse<Void> transportTaskHint(TransportTaskRequest request);

    JdCResponse<Boolean> uploadUnloadScanPhotoAboutEasyFreeze(GoodsPhotoInfoDto dto);
}
