package com.jd.bluedragon.distribution.jy.service.strand;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.*;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;

import java.util.List;

/**
 * 拣运app-滞留上报接口
 *
 * @author hujiping
 * @date 2023/3/27 4:04 PM
 */
public interface JyBizTaskStrandReportDealService {

    /**
     * 人工创建滞留上报任务
     * 
     * @param request
     * @return
     */
    InvokeResult<JyStrandReportTaskVO> artificialCreateStrandReportTask(JyStrandReportTaskCreateReq request);

    /**
     * 系统自建滞留上报任务
     * 
     * @param request
     * @return
     */
    InvokeResult<JyStrandReportTaskVO> systemCreateStrandReportTask(JyStrandReportTaskCreateReq request);

    /**
     * 取消任务
     * 
     * @param request
     * @return
     */
    InvokeResult<Void> cancelStrandReportTask(JyStrandReportTaskCreateReq request);

    /**
     * 获取滞留原因
     *
     * @return
     */
    InvokeResult<List<ConfigStrandReasonData>> queryStrandReason();

    /**
     * 拣运-滞留扫货方式
     * 
     * @return
     */
    InvokeResult<List<JyBizStrandScanTypeEnum>> queryStrandScanType();
    
    /**
     * 滞留扫描
     * 
     * @param scanRequest
     * @return
     */
    InvokeResult<JyStrandReportScanResp> strandScan(JyStrandReportScanReq scanRequest);

    /**
     * 取消滞留扫描
     * 
     * @param request
     * @return
     */
    InvokeResult<JyStrandReportScanResp> cancelStrandScan(JyStrandReportScanReq request);

    /**
     * 滞留上报提交
     *
     * @param scanRequest
     * @return
     */
    InvokeResult<Void> strandReportSubmit(JyStrandReportScanReq scanRequest);

    /**
     * 分页查询滞留上报任务
     * 
     * @param pageReq
     * @return
     */
    InvokeResult<JyStrandReportTaskPageResp> queryStrandReportTaskPageList(JyStrandReportTaskPageReq pageReq);

    /**
     * 查询滞留上报任务明细
     *
     * @param bizId
     * @return
     */
    InvokeResult<JyStrandReportTaskDetailVO> queryStrandReportTaskDetail(String bizId);

    /**
     * 分页查询任务已扫明细
     * 
     * @param detailPageReq
     * @return
     */
    InvokeResult<List<JyStrandReportScanVO>> queryPageStrandReportTaskDetail(JyStrandReportScanPageReq detailPageReq);

    /**
     * 滞留容器处理
     * 
     * @param jyBizStrandReportDetail
     */
    void scanContainerDeal(JyBizStrandReportDetailEntity jyBizStrandReportDetail);

    /**
     * 校验运输驳回处理是否已存在
     * 
     * @param transPlanCode
     * @return
     */
    boolean existCheck(String transPlanCode);
    
}
