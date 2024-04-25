package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptDisposeRecord;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskStatusEnum;

import java.util.Date;
import java.util.List;

public interface JyExceptionService {
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

    /**
     * 更新异常任务结果
     *
     * @param barCode 单号
     * @param operateErp 操作人
     * @param dateTime 时间
     * @param precessComplete 是否处理完成
     */
    void updateExceptionResult(String barCode, String operateErp, Date dateTime, boolean precessComplete);

    /**
     * 更新异常任务结果
     *
     * @param barCode 单号
     * @param operateErp 操作人
     * @param dateTime 时间
     * @param precessComplete 是否处理完成
     */
    void updateExceptionResult(String barCode, String operateErp, Date dateTime, boolean precessComplete, JyBizTaskExceptionCycleTypeEnum jyBizTaskExceptionCycleTypeEnum);

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

    void sendScheduleTaskStatusMsg(String bizId, String userErp,
                                   JyScheduleTaskStatusEnum status, DefaultJMQProducer producer);

    void recordLog(JyBizTaskExceptionCycleTypeEnum cycle, JyBizTaskExceptionEntity entity);

    void delivered(String bizId);

    PositionDetailRecord getPosition(String positionCode);

    String getGridRid(PositionDetailRecord data);

    void pushScrapTrace(JyBizTaskExceptionEntity exTaskEntity);

    /**
     * 获取bizId
     * @param businessInterceptReport 拦截记录
     * @return bizId结果包装
     * @author fanggang7
     * @time 2024-01-21 20:21:11 周日
     */
    String getBizId(BusinessInterceptReport businessInterceptReport);

    /**
     * 消费拦截报表明细数据
     * @return 处理结果
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    Result<Boolean> handleDmsBusinessInterceptReportUpload(BusinessInterceptReport businessInterceptReport);

    /**
     * 消费拦截处理消息
     * @param businessInterceptDisposeRecord 拦截处理数据
     * @return 处理结果
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    Result<Boolean> handleDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord);

    /**
     * 获取拦截任务明细
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    Result<JyExceptionInterceptDetailDto> getTaskDetailOfIntercept(ExpTaskCommonReq req);

    /**
     * 拦截任务处理
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    Result<Boolean> processTaskOfIntercept(ExpInterceptTaskProcessReq req);

    /**
     * 拦截任务-上传重量体积
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    Result<Boolean> processTaskOfInterceptSubmitWeightVolume(ExpInterceptTaskProcessSubmitWeightVolumeReq req);
}
