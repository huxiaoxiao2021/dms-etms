package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpBaseReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpReceiveReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskByIdReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskPageReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.jyexpection.request.StatisticsByGridReq;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDto;
import com.jd.bluedragon.common.dto.jyexpection.response.ProcessingNumByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionPrintDto;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jdl.basic.api.domain.position.PositionDetailRecord;

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

    void recordLog(JyBizTaskExceptionCycleTypeEnum cycle, JyBizTaskExceptionEntity entity);

    void delivered(String bizId);

    PositionDetailRecord getPosition(String positionCode);

    String getGridRid(PositionDetailRecord data);

    void pushScrapTrace(JyBizTaskExceptionEntity exTaskEntity);
}
