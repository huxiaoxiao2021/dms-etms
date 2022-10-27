package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionNoTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionFinishPreviewData;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionInterceptDto;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionTaskDetail;
import com.jd.bluedragon.distribution.jy.service.inspection.JyWarehouseInspectionService;
import com.jd.bluedragon.external.gateway.service.JyWarehouseInspectionGatewayService;
import com.jd.dms.workbench.utils.sdk.base.Result;

import javax.annotation.Resource;

/**
 * 接货仓验货服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-19 14:51:58 周三
 */
public class JyWarehouseInspectionGatewayServiceImpl implements JyWarehouseInspectionGatewayService {

    @Resource
    private JyWarehouseInspectionService jyWarehouseInspectionService;

    private <T> JdCResponse<T> retJdCResponse(Result<T> result) {
        return new JdCResponse<>(result.getCode(), result.getMessage(), result.getData());
    }

    /**
     * 创建无任务验货
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:23:19 周日
     */
    @Override
    public JdCResponse<InspectionTaskDetail> createNoTaskInspectionTask(InspectionNoTaskRequest request) {
        return retJdCResponse(jyWarehouseInspectionService.createNoTaskInspectionTask(request));
    }

    /**
     * 验货任务明细
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:23:19 周日
     */
    @Override
    public JdCResponse<InspectionTaskDetail> inspectionTaskDetail(InspectionCommonRequest request) {
        return retJdCResponse(jyWarehouseInspectionService.inspectionTaskDetail(request));
    }

    /**
     * 验货扫描
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    public JdVerifyResponse<Integer> inspectionScan(InspectionScanRequest request) {
        return jyWarehouseInspectionService.inspectionScan(request);
    }

    /**
     * 拦截单号明细
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    public JdCResponse<InspectionInterceptDto> interceptBarCodeDetail(InspectionCommonRequest request) {
        return retJdCResponse(jyWarehouseInspectionService.interceptBarCodeDetail(request));
    }

    /**
     * 验货完成前预览是否有异常数据
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    public JdCResponse<InspectionFinishPreviewData> inspectionFinishPreview(InspectionCommonRequest request) {
        return retJdCResponse(jyWarehouseInspectionService.inspectionFinishPreview(request));
    }

    /**
     * 验货完成
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    public JdCResponse<Boolean> submitInspectionCompletion(InspectionCommonRequest request) {
        return retJdCResponse(jyWarehouseInspectionService.submitInspectionCompletion(request));
    }
}
