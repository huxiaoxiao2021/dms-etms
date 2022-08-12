package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;

import java.util.List;

public class JyExceptionServiceImpl implements JyExceptionService {
    /**
     * 通用异常上报入口-扫描
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {
        return null;
    }

    /**
     * 按取件状态统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {
        return null;
    }

    /**
     * 网格待取件列表统计接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req) {
        return null;
    }

    /**
     * 取件进行中数据统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getReceivingCount(StatisticsByGridReq req) {
        return null;
    }

    /**
     * 任务列表接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getExceptionTaskPageList(ExpTaskPageReq req) {
        return null;
    }

    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> receive(ExpReceiveReq req) {
        return null;
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {
        return null;
    }

    /**
     * 处理任务接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> processTask(ExpTaskDetailReq req) {
        return null;
    }
}
