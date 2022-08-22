package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;
import com.jd.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JyExceptionGatewayServiceImpl implements JyExceptionGatewayService {
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {

        return JdCResponse.ok();
    }

    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {

        List<StatisticsByStatusDto> list = new ArrayList<>();
        for (JyExpStatusEnum value : JyExpStatusEnum.values()) {
            StatisticsByStatusDto dto = new StatisticsByStatusDto();
            dto.setStatus(value.getCode());
            dto.setCount(new Random().nextInt(100));
            list.add(dto);
        }

        return JdCResponse.ok(list);
    }

    @Override
    public JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req) {
        String json = "[{\"gridNo\":\"10\",\"areaCode\":\"LDPSFQ-001\",\"areaName\":\"落地配送分区\",\"tags\":[{\"code\":3,\"name\":\"三无\",\"style\":\"\"},{\"code\":0,\"name\":\"客户改址\",\"style\":\"\"}],\"pendingNum\":1231,\"timeoutNum\":1231},{\"gridNo\":\"10\",\"areaName\":\"落地配送分区\",\"tags\":[{\"code\":3,\"name\":\"三无\",\"style\":\"\"},{\"code\":0,\"name\":\"客户改址\",\"style\":\"\"}],\"pendingNum\":1231,\"timeoutNum\":1231}]";
        List<StatisticsByGridDto> list = JSON.parseArray(json, StatisticsByGridDto.class);

        return JdCResponse.ok(list);
    }

    /**
     * 取件进行中数据统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req) {
        return null;
    }

    /**
     * 任务列表接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req) {
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
