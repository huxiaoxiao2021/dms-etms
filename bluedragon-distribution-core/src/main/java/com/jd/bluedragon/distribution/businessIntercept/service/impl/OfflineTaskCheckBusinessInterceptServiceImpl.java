package com.jd.bluedragon.distribution.businessIntercept.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryRequest;
import com.jd.bluedragon.common.dto.send.response.CheckBeforeSendResponse;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.businessIntercept.service.IOfflineTaskCheckBusinessInterceptService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.external.gateway.service.SendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 离线任务处理，校验是否有拦截
 *
 * @author fanggang7
 * @time 2020-12-22 16:19:39 周二
 */
@Service("offlineTaskCheckBusinessInterceptService")
public class OfflineTaskCheckBusinessInterceptServiceImpl implements IOfflineTaskCheckBusinessInterceptService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private SendGatewayService sendGatewayService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    private List<Integer> sendTaskTypeList = new ArrayList<>(Arrays.asList(Task.TASK_TYPE_SEND_DELIVERY, Task.TASK_TYPE_ACARABILL_SEND_DELIVERY));

    /**
     * 处理离线任务，校验是否有拦截
     *
     * @param offlineLogRequest 离线请求
     * @return 处理结果
     */
    @Override

    public Response<Boolean> handleOfflineTask(OfflineLogRequest offlineLogRequest) {
        log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineTask param: {}", JSON.toJSONString(offlineLogRequest));
        Response<Boolean> result = new Response<>();
        result.setData(true);
        result.toSucceed();
        try {
            // 判断是否需要处理，目前只记录分拣、发货动作的数据
            int taskType = offlineLogRequest.getTaskType();
            if (Task.TASK_TYPE_SORTING.equals(taskType)) {
                // 如果是分拣
                this.handleOfflineSorting(offlineLogRequest);
            }
            if (sendTaskTypeList.contains(taskType)) {
                // 如果是发@JProfiler(jKey = "DMSWEB.OfflineTaskCheckBusinessInterceptServiceImpl.handleOfflineTask", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)货
                this.handleOfflineSend(offlineLogRequest);
            }
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineTask exception {}", e.getMessage(), e);
            result.toError("离线任务处理，校验是否有拦截处理异常");
        }
        return result;
    }

    private void handleOfflineSorting(OfflineLogRequest offlineLogRequest) {
        try {
            // 转变为pda操作对象
            PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
            // 在线状态
            pdaOperateRequest.setOnlineStatus(BusinessInterceptOnlineStatusEnum.OFFLINE.getCode());
            pdaOperateRequest.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
            pdaOperateRequest.setCreateSiteCode(offlineLogRequest.getSiteCode());
            pdaOperateRequest.setCreateSiteName(offlineLogRequest.getSiteName());
            pdaOperateRequest.setBoxCode(offlineLogRequest.getBoxCode());
            pdaOperateRequest.setPackageCode(offlineLogRequest.getPackageCode());
            pdaOperateRequest.setBusinessType(offlineLogRequest.getBusinessType());
            pdaOperateRequest.setOperateUserCode(offlineLogRequest.getUserCode());
            pdaOperateRequest.setOperateUserName(offlineLogRequest.getUserName());
            pdaOperateRequest.setOperateTime(offlineLogRequest.getOperateTime());
            pdaOperateRequest.setOperateType(offlineLogRequest.getOperateType());
            // 操作节点区分
            int operateNode = this.getOperateNode(offlineLogRequest);

            pdaOperateRequest.setOperateNode(operateNode);
            pdaOperateRequest.setIsLoss(0);
            // 走一遍校验链，得到拦截结果
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSorting pdaOperateRequest: {}", JSON.toJSONString(pdaOperateRequest));
            SortingJsfResponse checkResult = sortingCheckService.sortingCheckAndReportIntercept(pdaOperateRequest);
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSorting checkResult: {}", JSON.toJSONString(checkResult));
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSorting sortingCheckAndReportIntercept throw exception {}", e.getMessage(), e);
        }
    }

    /**
     * 离线发货处理逻辑
     * @param offlineLogRequest 离线数据
     * @author fanggang7
     * @time 2021-03-24 18:06:18 周三
     */
    private void handleOfflineSend(OfflineLogRequest offlineLogRequest) {
        try {
            if(offlineLogRequest.getBizSource() == null){
                return;
            }
            // 离线老发货
            if (SendBizSourceEnum.OFFLINE_OLD_SEND.getCode().equals(offlineLogRequest.getBizSource())) {
                this.handleOfflineOldSend(offlineLogRequest);
            }
            // 离线新发货
            if (SendBizSourceEnum.OFFLINE_NEW_SEND.getCode().equals(offlineLogRequest.getBizSource())) {
                this.handleOfflineSendNew(offlineLogRequest);
            }
            // 离线组板发货
            if (SendBizSourceEnum.OFFLINE_BOARD_SEND.getCode().equals(offlineLogRequest.getBizSource())) {
            }
            // 离线空铁一车一单发货
            if (SendBizSourceEnum.OFFLINE_AR_NEW_SEND.getCode().equals(offlineLogRequest.getBizSource())) {
            }
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSend throw exception {}", e.getMessage(), e);
        }
    }

    /**
     * 离线老发货处理逻辑
     * @param offlineLogRequest 离线数据
     * @author fanggang7
     * @time 2021-03-24 18:06:18 周三
     */
    private void handleOfflineOldSend(OfflineLogRequest offlineLogRequest) {
        try {
            DeliveryRequest deliveryRequest = new DeliveryRequest();
            deliveryRequest.setBoxCode(offlineLogRequest.getBoxCode());
            deliveryRequest.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
            deliveryRequest.setTurnoverBoxCode(offlineLogRequest.getTurnoverBoxCode());
            deliveryRequest.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
            CurrentOperate currentOperate = new CurrentOperate();
            currentOperate.setSiteCode(offlineLogRequest.getSiteCode());
            currentOperate.setSiteName(offlineLogRequest.getSiteName());
            currentOperate.setOperateTime(DateUtil.parseDateByStr(offlineLogRequest.getOperateTime(), DateUtil.FORMAT_DATE_TIME));
            deliveryRequest.setCurrentOperate(currentOperate);
            User user = new User();
            user.setUserCode(offlineLogRequest.getUserCode());
            user.setUserName(offlineLogRequest.getUserName());
            deliveryRequest.setUser(user);
            // 走一遍校验链，得到拦截结果
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineOldSend deliveryRequest: {}", JSON.toJSONString(deliveryRequest));
            JdVerifyResponse<CheckBeforeSendResponse> checkResult = sendGatewayService.checkBeforeSend(deliveryRequest);
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineOldSend checkBeforeSend: {}", JSON.toJSONString(checkResult));
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineOldSend singleSendCheckAndReportIntercept throw exception {}", e.getMessage(), e);
        }
    }

    /**
     * 离线新发货处理逻辑
     * @param offlineLogRequest 离线数据
     * @author fanggang7
     * @time 2021-03-24 18:06:18 周三
     */
    private void handleOfflineSendNew(OfflineLogRequest offlineLogRequest) {
        try {
            // 走一遍发货校验，得到拦截结果
            SortingCheck sortingCheck = new SortingCheck();
            sortingCheck.setOnlineStatus(BusinessInterceptOnlineStatusEnum.OFFLINE.getCode());
            sortingCheck.setOperateNode(OperateNodeConstants.SEND);
            sortingCheck.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
            sortingCheck.setCreateSiteCode(offlineLogRequest.getSiteCode());
            sortingCheck.setBoxCode(offlineLogRequest.getBoxCode());
            sortingCheck.setPackageCode(offlineLogRequest.getPackageCode());
            sortingCheck.setBusinessType(offlineLogRequest.getBusinessType());
            sortingCheck.setOperateUserCode(offlineLogRequest.getUserCode());
            sortingCheck.setOperateUserName(offlineLogRequest.getUserName());
            sortingCheck.setOperateTime(offlineLogRequest.getOperateTime());
            sortingCheck.setBizSourceType(offlineLogRequest.getBizSource());
            if (sortingCheck.getCreateSiteCode() != null && siteService.getCRouterAllowedList().contains(sortingCheck.getCreateSiteCode())) {
                //判断批次号目的地的站点类型，是64的走新逻辑，非64的走老逻辑
                BaseStaffSiteOrgDto siteInfo = baseService.queryDmsBaseSiteByCode(sortingCheck.getReceiveSiteCode() + "");
                if (siteInfo == null || siteInfo.getSiteType() != 64) {
                    sortingCheck.setOperateType(1);
                } else {
                    sortingCheck.setOperateType(Constants.OPERATE_TYPE_NEW_PACKAGE_SEND);
                }
            } else {
                sortingCheck.setOperateType(1);
            }
            // 走一遍校验链，得到拦截结果
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSendNew sortingCheck: {}", JSON.toJSONString(sortingCheck));
            SortingJsfResponse singleSendCheckResult = sortingCheckService.singleSendCheckAndReportIntercept(sortingCheck);
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSendNew singleSendCheckResult: {}", JSON.toJSONString(singleSendCheckResult));
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineSendNew singleSendCheckAndReportIntercept throw exception {}", e.getMessage(), e);
        }
    }

    /**
     * 组板发货处理逻辑
     * @param request 离线数据
     * @author fanggang7
     * @time 2021-03-24 18:06:50 周三
     */
    private void handleOfflineBoardCombinationSend(OfflineLogRequest request) {
        try {
            // 走一遍发货校验，得到拦截结果
            BoardCombinationRequest boardCombinationRequest = new BoardCombinationRequest();
            // 走一遍校验链，得到拦截结果
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineBoardCombinationSend boardCombinationRequest: {}", JSON.toJSONString(boardCombinationRequest));
            BoardCombinationJsfResponse boardCombinationCheckResult = sortingCheckService.boardCombinationCheckAndReportIntercept(boardCombinationRequest);
            log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineBoardCombinationSend boardCombinationCheckResult: {}", JSON.toJSONString(boardCombinationCheckResult));
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineBoardCombinationSend boardCombinationCheckAndReportIntercept throw exception {}", e.getMessage(), e);
        }
    }

    /**
     * 获取校验链上下文的操作节点值
     *
     * @param offlineLogRequest 离线请求参数
     * @return 判定结果
     * @author fanggang7
     * @time 2020-12-22 17:22:26 周二
     */
    private int getOperateNode(OfflineLogRequest offlineLogRequest) {
        int operateNode = 0;
        if (Objects.equals(Task.TASK_TYPE_SORTING, offlineLogRequest.getTaskType())) {
            operateNode = OperateNodeConstants.SORTING;
        }
        if (sendTaskTypeList.contains(offlineLogRequest.getTaskType())) {
            operateNode = OperateNodeConstants.SEND;
        }
        return operateNode;
    }

    @Qualifier("dmsBusinessOperateOfflineTaskSendProducer")
    @Autowired
    private DefaultJMQProducer dmsBusinessOperateOfflineTaskSendProducer;

    @Override
    public Response<Boolean> sendOfflineTaskMq(Task task) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        String body = task.getBody();
        // 发出离线任务mq，后续消费此消息进行后续处理
        try {
            OfflineLogRequest[] offlineLogRequests = JsonHelper.jsonToArray(body, OfflineLogRequest[].class);
            if (offlineLogRequests == null) {
                return result;
            }
            List<Message> messageList = new ArrayList<>();
            for (OfflineLogRequest offlineLogRequest : offlineLogRequests) {
                Message message = new Message();
                message.setBusinessId(offlineLogRequest.getPackageCode());
                message.setText(JSON.toJSONString(offlineLogRequest));
                log.info("batchSendOfflineTask2Mq text: {}", message.getText());
                message.setTopic(dmsBusinessOperateOfflineTaskSendProducer.getTopic());
                messageList.add(message);
            }
            dmsBusinessOperateOfflineTaskSendProducer.batchSend(messageList);
        } catch (JMQException e) {
            result.toError("sendOfflineSortingTaskMq exception " + e.getMessage());
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl sendOfflineSortingTaskMq exception :{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发出离线分拣处理消息
     *
     * @param offlineLogRequests 离线数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-23 15:39:43 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.OfflineTaskCheckBusinessInterceptServiceImpl.batchSendOfflineTaskMq", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Response<Boolean> batchSendOfflineTaskMq(List<OfflineLogRequest> offlineLogRequests) {
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        // 发出离线任务mq，后续消费此消息进行后续处理
        try {
            List<Message> messageList = new ArrayList<>();
            for (OfflineLogRequest offlineLogRequest : offlineLogRequests) {
                if(!uccPropertyConfiguration.getOfflineTaskReportInterceptNeedHandle(offlineLogRequest.getSiteCode())){
                    continue;
                }
                Message message = new Message();
                message.setBusinessId(offlineLogRequest.getPackageCode());
                message.setText(JSON.toJSONString(offlineLogRequest));
                log.info("batchSendOfflineTask2Mq text: {}", message.getText());
                message.setTopic(dmsBusinessOperateOfflineTaskSendProducer.getTopic());
                messageList.add(message);
            }
            dmsBusinessOperateOfflineTaskSendProducer.batchSend(messageList);
        } catch (JMQException e) {
            result.toError("batchSendOfflineTaskMq exception " + e.getMessage());
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl batchSendOfflineTaskMq exception :{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发出离线分拣处理消息
     *
     * @param offlineLogRequest 离线数据
     * @return 处理结果
     * @author fanggang7
     * @time 2021-03-23 15:39:43 周二
     */
    @Override
    @JProfiler(jKey = "DMSWEB.OfflineTaskCheckBusinessInterceptServiceImpl.sendOfflineTaskMq", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Response<Boolean> sendOfflineTaskMq(OfflineLogRequest offlineLogRequest) {
        return this.batchSendOfflineTaskMq(new ArrayList<>(Collections.singletonList(offlineLogRequest)));
    }
}

