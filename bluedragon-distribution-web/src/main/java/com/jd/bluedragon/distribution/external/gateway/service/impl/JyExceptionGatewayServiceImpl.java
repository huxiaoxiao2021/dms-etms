package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JyExceptionGatewayServiceImpl implements JyExceptionGatewayService {
    @Autowired
    private JyExceptionService jyExceptionService;


    /**
     * 通用异常上报入口-扫描
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {

        return jyExceptionService.uploadScan(req);
    }

    /**
     * 按取件状态统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {

        return jyExceptionService.statisticsByStatus(req);
    }

    /**
     * 网格待取件列表统计接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req) {

        return jyExceptionService.getGridStatisticsPageList(req);
    }

    /**
     * 取件进行中数据统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req) {

        return jyExceptionService.getReceivingCount(req);
    }

    /**
     * 释放进行中的人数
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> releaseReceivingCount(ExpTaskPageReq req) {
        return jyExceptionService.releaseReceivingCount(req);
    }

    /**
     * 任务列表接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req) {

        return jyExceptionService.getExceptionTaskPageList(req);
    }

    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> receive(ExpReceiveReq req) {

        return jyExceptionService.receive(req);
    }

    /**
     * 按条码查询
     *
     */
    @Override
    public JdCResponse<ExpTaskDto> queryByBarcode(ExpReceiveReq req) {
        return jyExceptionService.queryByBarcode(req.getBarCode());
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {

        return jyExceptionService.getTaskDetail(req);
    }

    /**
     * 处理任务接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> processTask(ExpTaskDetailReq req) {

        return jyExceptionService.processTask(req);
    }

}
