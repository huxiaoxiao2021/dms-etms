package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionDamageEnum;
import com.jd.bluedragon.distribution.barcode.service.BarcodeService;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionCountDto;
import com.jd.bluedragon.distribution.jy.exception.JyExpCustomerReturnMQ;
import com.jd.bluedragon.distribution.jy.service.exception.JyContrabandExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JySanwuExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.impl.JyScrappedExceptionServiceImpl;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.external.gateway.service.JyExceptionGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JyExceptionGatewayServiceImpl implements JyExceptionGatewayService {
    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private JyScrappedExceptionServiceImpl jyScrappedExceptionService;

    @Autowired
    private JySanwuExceptionService jySanwuExceptionService;

    @Autowired
    private JyContrabandExceptionService jyContrabandExceptionService;

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    /**
     * 通用异常上报入口-扫描
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.uploadScan", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {

        return jyExceptionService.uploadScan(req);
    }

    /**
     * 按取件状态统计
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.statisticsByStatus", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {

        return jyExceptionService.statisticsByStatus(req);
    }

    /**
     * 网格待取件列表统计接口
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getGridStatisticsPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req) {

        return jyExceptionService.getGridStatisticsPageList(req);
    }

    /**
     * 取件进行中数据统计
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getReceivingCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req) {

        return jyExceptionService.getReceivingCount(req);
    }

    /**
     * 释放进行中的人数
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.releaseReceivingCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> releaseReceivingCount(ExpTaskPageReq req) {
        return jyExceptionService.releaseReceivingCount(req);
    }

    /**
     * 任务列表接口
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getExceptionTaskPageList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req) {

        return jyExceptionService.getExceptionTaskPageList(req);
    }

    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.receive", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> receive(ExpReceiveReq req) {

        return jyExceptionService.receive(req);
    }

    /**
     * 按条码查询
     *
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.queryByBarcode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<ExpTaskDto> queryByBarcode(ExpReceiveReq req) {
        return jyExceptionService.queryByBarcode(req);
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getTaskDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {

        return jyExceptionService.getTaskDetail(req);
    }

    /**
     * 处理任务接口
     *
     * @param req
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.processTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Object> processTask(ExpTaskDetailReq req) {

        return jyExceptionService.processTask(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.queryProductName", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<DmsBarCode>> queryProductName(String barCode) {
        if (StringUtils.isEmpty(barCode)){
            return JdCResponse.fail("缺少查询参数");
        }
        String[] barCodes = barCode.split(",");
        List<com.jd.bluedragon.distribution.barcode.domain.DmsBarCode> queryResults = barcodeService.query(barCodes);
        if (CollectionUtils.isEmpty(queryResults)){
            return JdCResponse.fail("查询失败,查询不到商品信息");
        }
        //取第一条
        List<DmsBarCode> queryResult = Lists.newArrayList();
        Set<String> codeSet = Sets.newHashSet();
        for (com.jd.bluedragon.distribution.barcode.domain.DmsBarCode dmsBarCode:queryResults){
            if (codeSet.contains(dmsBarCode.getBarcode())){
                continue;
            }
            DmsBarCode result = new DmsBarCode();
            BeanUtils.copyProperties(dmsBarCode,result);
            queryResult.add(result);
            codeSet.add(dmsBarCode.getBarcode());
        }
        JdCResponse<List<DmsBarCode>> result = JdCResponse.ok();
        result.setData(queryResult);
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getJyExceptionScrappedTypeList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<JyExceptionScrappedTypeDto>> getJyExceptionScrappedTypeList() {
        return jyScrappedExceptionService.getJyExceptionScrappedTypeList();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getJyExceptionPackageTypeList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<JyExceptionPackageTypeDto>> getJyExceptionPackageTypeList(String barCode) {
        return jyDamageExceptionService.getJyExceptionPackageTypeList(barCode);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.processTaskOfscrapped", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> processTaskOfscrapped(ExpScrappedDetailReq req) {
        return jyScrappedExceptionService.processTaskOfscrapped(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getTaskDetailOfscrapped", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<ExpScrappedDetailDto> getTaskDetailOfscrapped(ExpTaskByIdReq req) {
        return jyScrappedExceptionService.getTaskDetailOfscrapped(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.checkExceptionPrincipal", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> checkExceptionPrincipal(ExpBaseReq req) {
        return jyExceptionService.checkExceptionPrincipal(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.exceptionTaskCheckByExceptionType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req) {
        return jyExceptionService.exceptionTaskCheckByExceptionType(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getExpTaskStatisticsOfWaitReceiveByPage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>> getExpTaskStatisticsOfWaitReceiveByPage(ExpTaskStatisticsReq req) {
        return jySanwuExceptionService.getExpTaskStatisticsOfWaitReceiveByPage(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getWaitReceiveSanwuExpTaskByPage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ExpTaskDto>> getWaitReceiveSanwuExpTaskByPage(ExpTaskStatisticsDetailReq req) {
        return jySanwuExceptionService.getWaitReceiveSanwuExpTaskByPage(req);
    }



    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getExpSignInUserByPage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ExpSignUserResp>> getExpSignInUserByPage(ExpSignUserReq req) {
        return jySanwuExceptionService.getExpSignInUserByPage(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.assignExpTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> assignExpTask(ExpTaskAssignRequest req) {
        return jySanwuExceptionService.assignExpTask(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getAssignExpTaskCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Integer> getAssignExpTaskCount(ExpBaseReq req) {
        return jySanwuExceptionService.getAssignExpTaskCount(req);
    }
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.processTaskOfDamage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req) {
        return jyDamageExceptionService.processTaskOfDamage(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getToProcessDamageCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(String positionCode){
        return jyDamageExceptionService.getToProcessDamageCount(positionCode);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.readToProcessDamage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> readToProcessDamage(String positionCode) {
        return jyDamageExceptionService.readToProcessDamage(positionCode);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getTaskDetailOfDamage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<JyExceptionDamageDto> getTaskDetailOfDamage(ExpDamageDetailReq req){
        return jyDamageExceptionService.getTaskDetailOfDamage(req);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.writeToProcessDamage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> writeToProcessDamage(String bizId){
        jyDamageExceptionService.writeToProcessDamage(bizId);
        return JdCResponse.ok();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.dealExpDamageInfoByAbnormalReportOutCall", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> dealExpDamageInfoByAbnormalReportOutCall(QcReportJmqDto qcReportJmqDto) {
        jyDamageExceptionService.dealExpDamageInfoByAbnormalReportOutCall(qcReportJmqDto);
        return JdCResponse.ok();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.dealCustomerReturnDamageResult", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> dealCustomerReturnDamageResult(JyExpCustomerReturnMQ returnMQ) {
        jyDamageExceptionService.dealCustomerReturnDamageResult(returnMQ);
        return JdCResponse.ok();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.processTaskOfContraband", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req) {
        return jyContrabandExceptionService.processTaskOfContraband(req);
    }

    @Override
    public JdCResponse<List<JyExceptionCountDto>> getExceptionCountValue(String startTime, String endTime) {
        JdCResponse<List<JyExceptionCountDto>> result = new JdCResponse<>();
        result.toSucceed();
        try{
            int damageCount = jyContrabandExceptionService.getDamageCountByTime(startTime, endTime);
            int contrabandCount = jyDamageExceptionService.getContrabandCountByTime(startTime, endTime);

            JyExceptionCountDto damageDto = new JyExceptionCountDto();
            damageDto.setExceptionName("破损");
            damageDto.setCount(damageCount);

            JyExceptionCountDto contrabandDto = new JyExceptionCountDto();
            contrabandDto.setExceptionName("违禁品");
            contrabandDto.setCount(contrabandCount);
            List<JyExceptionCountDto> list = new ArrayList<>();
            list.add(damageDto);
            list.add(contrabandDto);
            result.setData(list);
            return  result;
        }catch (Exception e){
            result.toError("取数异常");
            return result;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionGatewayServiceImpl.getConsumables", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<com.jd.bluedragon.distribution.jy.dto.Consumable>> getConsumables() {
        return jyDamageExceptionService.getConsumables();
    }
}
