package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.bluedragon.distribution.print.domain.RePrintRecordMq;
import com.jd.ps.data.epf.dto.ExpefNotify;

import java.util.Date;
import java.util.List;

public interface JyExceptionService {
    /**
     * 通用异常上报入口-扫描
     */
    JdCResponse<Object> uploadScan(ExpUploadScanReq req);


    void recordLog(JyBizTaskExceptionCycleTypeEnum cycle, JyBizTaskExceptionEntity entity);
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
    JdCResponse<ExpTaskDto> queryByBarcode(Integer type,String barcode);

    /**
     * 任务明细
     */
    JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req);

    /**
     * 处理任务接口-三无
     */
    JdCResponse<Object> processTask(ExpTaskDetailReq req);

    /**
     * 三无系统结果通知
     * @param mqDto
     */
    void expefNotifyProcesser(ExpefNotify mqDto);

    /**
     * 打印成功后续处理
     * @param printDto
     */
    void printSuccess(JyExceptionPrintDto printDto);

    /**
     * 查询超时异常任务并通知场地负责人
     */
    void queryOverTimeExceptionAndNotice();

    /**
     * 查询已领取的生鲜报废任务明细并通知领取人
     */
    void queryFreshScrapDetailAndNotice();

    /**
     * 根据处理时间查询报废处理人ERP
     * 
     * @param queryStartTime 查询开始时间
     * @param queryEndTime 查询结束时间
     * @return
     */
    List<String> queryScrapHandlerErp(Date queryStartTime, Date queryEndTime);

    /**
     * 根据处理时间查询报废人的报废任务详情
     * 
     * @param handlerErp 报废人ERP
     * @param queryStartTime 查询开始时间
     * @param queryEndTime 查询结束时间
     * @return
     */
    List<JyBizTaskExceptionEntity> queryScrapDetailByCondition(String handlerErp, Date queryStartTime, Date queryEndTime);

    /**
     * 处理客服回传消息
     * 
     * @param jyExCustomerNotifyMQ
     */
    void dealCustomerNotifyResult(JyExCustomerNotifyMQ jyExCustomerNotifyMQ);
}
