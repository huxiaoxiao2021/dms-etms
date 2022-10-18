package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.distribution.barcode.domain.DmsBarCode;
import com.jd.bluedragon.distribution.barcode.service.BarcodeService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

public class JyExceptionGatewayServiceImpl implements JyExceptionGatewayService {
    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private BarcodeService barcodeService;

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

    @Override
    public JdCResponse<List<DmsBarCode>> queryProductName(String barCode) {
        if (StringUtils.isEmpty(barCode)){
            return JdCResponse.fail("缺少查询参数");
        }
        String[] barCodes = barCode.split(",");
        List<DmsBarCode> queryResults = barcodeService.query(barCodes);
        if (CollectionUtils.isEmpty(queryResults)){
            return JdCResponse.fail("查询失败,查询不到商品信息");
        }
        //取第一条
        List<DmsBarCode> queryResult = Lists.newArrayList();
        Set<String> codeSet = Sets.newHashSet();
        for (DmsBarCode dmsBarCode:queryResults){
            if (codeSet.contains(dmsBarCode.getBarcode())){
                continue;
            }
            queryResult.add(dmsBarCode);
            codeSet.add(dmsBarCode.getBarcode());
        }
        JdCResponse<List<DmsBarCode>> result = JdCResponse.ok();
        result.setData(queryResult);
        return result;
    }

}
