package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.DmsBarCode;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpScrappedDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpSignUserResp;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskStatisticsOfWaitReceiveDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionPackageTypeDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionScrappedTypeDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ProcessingNumByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionDamageEnum;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionCountDto;
import com.jd.bluedragon.distribution.jy.exception.JyExpCustomerReturnMQ;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;

import java.util.List;

/**
 * 作业app异常岗
 */
public interface JyExceptionGatewayService {

    /**
     * 通用异常上报入口-扫描
     */
    JdCResponse<Object> uploadScan(ExpUploadScanReq req);

    /**
     * 按取件状态统计
     */
    JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req);

    /**
     * 网格待取件列表统计接口
     */
    JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req);

    /**
     * 取件进行中数据统计
     */
    JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req);

    /**
     * 释放进行中的人数
     *
     */
    JdCResponse<Object> releaseReceivingCount(ExpTaskPageReq req);

    /**
     * 任务列表接口
     */
    JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req);

    /**
     * 任务领取接口
     */
    JdCResponse<Object> receive(ExpReceiveReq req);

    /**
     * 按条码查询
     */
    JdCResponse<ExpTaskDto> queryByBarcode(ExpReceiveReq req);

    /**
     * 任务明细
     */
    JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req);

    /**
     * 处理任务接口
     */
    JdCResponse<Object> processTask(ExpTaskDetailReq req);

    /**
     * 查下商品名称
     * @return
     */
    JdCResponse<List<DmsBarCode>> queryProductName(String barCode);

    /**
     * 获取报废类型集合
     * @return
     */
    JdCResponse<List<JyExceptionScrappedTypeDto>> getJyExceptionScrappedTypeList();

    /**
     * 获取破损包裹类型集合
     * @return
     */
    JdCResponse<List<JyExceptionPackageTypeDto>> getJyExceptionPackageTypeList(String barCode);

    /**
     * 报废处理任务接口
     * @param req
     * @return
     */
    JdCResponse<Boolean> processTaskOfscrapped(ExpScrappedDetailReq req);

    /**
     * 获取生鲜待处理任务详情
     * @param req
     * @return
     */
    JdCResponse<ExpScrappedDetailDto> getTaskDetailOfscrapped(ExpTaskByIdReq req);

    /**
     *校验当前登录erp是否是负责人
     * @return
     */
    JdCResponse<Boolean> checkExceptionPrincipal(ExpBaseReq req);

    /**
     * 异常任务类型校验
     * @param req
     * @return
     */
    JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req);


    /**
     * 获取超时未领取任务统计接口
     * @param req
     * @return
     */
    JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>> getExpTaskStatisticsOfWaitReceiveByPage(ExpTaskStatisticsReq req);

    /**
     * 获取超时未领取任务列表
     * @param req
     * @return
     */
    JdCResponse<List<ExpTaskDto>> getWaitReceiveSanwuExpTaskByPage(ExpTaskStatisticsDetailReq req);



    /**
     * 获取异常岗签到用户
     * @param req
     * @return
     */
    JdCResponse<List<ExpSignUserResp>> getExpSignInUserByPage(ExpSignUserReq req);

    /**
     * 指派任务给指定人
     * @param req
     * @return
     */
    JdCResponse<Boolean> assignExpTask(ExpTaskAssignRequest req);

    /**
     * 获取指派任务数
     * @param req
     * @return
     */
    JdCResponse<Integer> getAssignExpTaskCount(ExpBaseReq req);

    /**
     * 破损任务处理
     */
    JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req);

    /**
     * 获取待处理和新增的破损异常数量
     * @return
     */
    JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(String positionCode);

    /**
     * 读取破损异常为已读
     * @param positionCode
     * @return
     */
    JdCResponse<Boolean> readToProcessDamage(String positionCode);

    /**
     * 获取破损任务详情
     * @param req
     * @return
     */
    JdCResponse<JyExceptionDamageDto> getTaskDetailOfDamage(ExpDamageDetailReq req);

    JdCResponse<Boolean> writeToProcessDamage(String bizId);

    /**
     * 根据质控异常提报mq 处理破损异常
     *
     * @param qcReportJmqDto
     * @return
     */
    JdCResponse<Boolean> dealExpDamageInfoByAbnormalReportOutCall(QcReportJmqDto qcReportJmqDto);


    JdCResponse<Boolean> dealCustomerReturnDamageResult(JyExpCustomerReturnMQ returnMQ);

    /**
     * 违禁品任务处理
     */
    JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req);

    JdCResponse<List<JyExceptionCountDto>> getExceptionCountValue(String startTime, String endTime);

    /**
     * 破损耗材明细
     */
    JdCResponse<List<com.jd.bluedragon.distribution.jy.dto.Consumable>> getConsumables();

}
