package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.*;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;

import java.util.List;

/**
 * 拣运app-滞留上报网关接口
 *
 * @author hujiping
 * @date 2023/3/28 3:18 PM
 */
public interface JyStrandReportGatewayService {

    /**
     * 人工创建滞留上报任务
     * 
     * @param request
     * @return
     */
    JdCResponse<Void> artificialCreateStrandReportTask(JyStrandReportTaskCreateReq request);

    /**
     * 取消任务
     *
     * @param request
     * @return
     */
    JdCResponse<Void> cancelStrandReportTask(JyStrandReportTaskCreateReq request);

    /**
     * 获取滞留原因
     *
     * @return
     */
    JdCResponse<List<ConfigStrandReasonData>> queryStrandReason();

    /**
     * 拣运-滞留扫货方式
     *
     * @return
     */
    JdCResponse<List<JyBizStrandScanTypeEnum>> queryStrandScanType();

    /**
     * 滞留扫描
     *
     * @param scanRequest
     * @return
     */
    JdCResponse<JyStrandReportScanResp> strandScan(JyStrandReportScanReq scanRequest);

    /**
     * 取消滞留扫描
     *
     * @param request
     * @return
     */
    JdCResponse<JyStrandReportScanResp> cancelStrandScan(JyStrandReportScanReq request);

    /**
     * 滞留上报提交
     *
     * @param scanRequest
     * @return
     */
    JdCResponse<Void> strandReportSubmit(JyStrandReportScanReq scanRequest);

    /**
     * 分页查询滞留上报任务
     *
     * @param pageReq
     * @return
     */
    JdCResponse<JyStrandReportTaskPageResp> queryStrandReportTaskPageList(JyStrandReportTaskPageReq pageReq);

    /**
     * 分页查询任务已扫明细
     * 
     * @param detailPageReq
     * @return
     */
    JdCResponse<List<JyStrandReportScanVO>> queryPageStrandReportTaskDetail(JyStrandReportScanPageReq detailPageReq);

    /**
     * 查询滞留上报任务明细
     *
     * @param bizId
     * @return
     */
    JdCResponse<JyStrandReportTaskDetailVO> queryStrandReportTaskDetail(String bizId);
}
