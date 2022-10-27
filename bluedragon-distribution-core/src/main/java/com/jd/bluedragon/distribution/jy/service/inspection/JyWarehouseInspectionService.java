package com.jd.bluedragon.distribution.jy.service.inspection;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionNoTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionFinishPreviewData;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionInterceptDto;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionTaskDetail;
import com.jd.dms.workbench.utils.sdk.base.Result;

/**
 * 接货仓验货服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-09 14:19:52 周日
 */
public interface JyWarehouseInspectionService {

    /**
     * 创建无任务验货
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:23:19 周日
     */
    Result<InspectionTaskDetail> createNoTaskInspectionTask(InspectionNoTaskRequest request);

    /**
     * 验货任务明细
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:23:19 周日
     */
    Result<InspectionTaskDetail> inspectionTaskDetail(InspectionCommonRequest request);

    /**
     * 验货扫描
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    JdVerifyResponse<Integer> inspectionScan(InspectionScanRequest request);

    /**
     * 拦截单号明细
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    Result<InspectionInterceptDto> interceptBarCodeDetail(InspectionCommonRequest request);

    /**
     * 验货完成前预览是否有异常数据
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    Result<InspectionFinishPreviewData> inspectionFinishPreview(InspectionCommonRequest request);

    /**
     * 验货完成
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    Result<Boolean> submitInspectionCompletion(InspectionCommonRequest request);
}
