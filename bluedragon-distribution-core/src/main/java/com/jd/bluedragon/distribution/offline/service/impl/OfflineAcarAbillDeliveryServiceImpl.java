package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.offline.service.OfflineService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 一车一单离线发货
 * Created by shipeilin on 2017/9/26.
 */
@Service("offlineAcarAbillDeliveryService")
public class OfflineAcarAbillDeliveryServiceImpl implements OfflineService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private OfflineLogService offlineLogService;

    /**
     * 操作日志service
     */
    @Autowired
    private OperationLogService operationLogService;

    /**
     * 一车一单离线发货操作备注
     */
    public static final String OFFLINE_DELIVERY_REMARK = "一车一单离线发货";

    @Override
    public int parseToTask(OfflineLogRequest request) {
        if (request == null || request.getBoxCode() == null || request.getSiteCode() == null || request.getBusinessType() == null) {
            this.logger.error("一车一单离线发货 --> 传入参数有误：" + JsonHelper.toJson(request));
            return Constants.RESULT_FAIL;
        }

        if (checkReceiveSiteCode(request)) {
            this.logger.info("一车一单离线发货 --> 开始写入发货信息");
            if (BusinessUtil.isBoardCode(request.getBoxCode())) {
                //一车一单下的组板发货
                Task task = new Task();
                task.setBody(JsonHelper.toJson(toSendM(request)));
                deliveryService.doBoardDelivery(task);
            } else {
                //一车一单发货
                if (Task.TASK_TYPE_AR_RECEIVE_AND_SEND.equals(request.getTaskType())) {
                    deliveryService.offlinePackageSend(SendBizSourceEnum.OFFLINE_AR_NEW_SEND, toSendM(request));
                } else {
                    deliveryService.offlinePackageSend(SendBizSourceEnum.OFFLINE_NEW_SEND, toSendM(request));
                }
            }

            offlineLogService.addOfflineLog(requestToOffline(request, Constants.RESULT_SUCCESS));
            operationLogService.add(requestConvertOperationLog(request));
            this.logger.info("一车一单离线发货 --> 结束写入发货信息");
            return Constants.RESULT_SUCCESS;
        }

        return Constants.RESULT_FAIL;
    }


    /**
     * 设置目的站点
     * @param request
     * @return
     */
    private boolean checkReceiveSiteCode(OfflineLogRequest request) {
        Integer receiveSiteCode = null;
        Boolean result = Boolean.TRUE;
        String sendCode = request.getBatchCode();

        if(StringUtils.isNotBlank(sendCode)){
            receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
        }
        if (checkBaseSite(receiveSiteCode)) {
            request.setReceiveSiteCode(receiveSiteCode);
        }else{
            logger.warn("一车一单离线发货获取目的站点失败：" + JsonHelper.toJson(request));
            result = Boolean.FALSE;
        }
        return result;
    }


    private boolean checkBaseSite(Integer receiveSiteCode) {
        if (receiveSiteCode == null || receiveSiteCode.equals(0)) {
            return Boolean.FALSE;
        }

        BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
                .getBaseSiteBySiteId(receiveSiteCode);

        if (baseStaffSiteOrgDto == null
                || baseStaffSiteOrgDto.getSiteCode() == null) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private SendM toSendM(OfflineLogRequest offlineLogRequest) {

        SendM sendM = new SendM();
        if(BusinessUtil.isBoardCode(offlineLogRequest.getBoxCode())) {//一车一单下的组板发货
            sendM.setBoardCode(offlineLogRequest.getBoxCode());
        }else{//一车一单发货
            sendM.setBoxCode(offlineLogRequest.getBoxCode());
        }
        sendM.setCreateSiteCode(offlineLogRequest.getSiteCode());
        sendM.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
        sendM.setCreateUserCode(offlineLogRequest.getUserCode());
        sendM.setSendType(offlineLogRequest.getBusinessType());
        sendM.setCreateUser(offlineLogRequest.getUserName());
        sendM.setSendCode(offlineLogRequest.getBatchCode());
        sendM.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        Date operateTime = DateHelper.parseDate(offlineLogRequest.getOperateTime(), Constants.DATE_TIME_MS_FORMAT);
        operateTime.setTime(operateTime.getTime() + Constants.DELIVERY_DELAY_TIME);
        sendM.setOperateTime(operateTime);
        sendM.setTurnoverBoxCode(offlineLogRequest.getTurnoverBoxCode());
        sendM.setTransporttype(offlineLogRequest.getTransporttype());
        sendM.setYn(1);

        return sendM;
    }

    private OfflineLog requestToOffline(OfflineLogRequest offlineLogRequest, Integer status) {
        if (offlineLogRequest == null) {
            return null;
        }
        OfflineLog offlineLog = new OfflineLog();
        offlineLog.setBoxCode(offlineLogRequest.getBoxCode());
        offlineLog.setBusinessType(offlineLogRequest.getBusinessType());
        offlineLog.setCreateSiteCode(offlineLogRequest.getSiteCode());
        offlineLog.setCreateSiteName(offlineLogRequest.getSiteName());
        offlineLog.setCreateUser(offlineLogRequest.getUserName());
        offlineLog.setCreateUserCode(offlineLogRequest.getUserCode());
        offlineLog.setExceptionType(offlineLogRequest.getExceptionType());
        offlineLog.setOperateTime(DateHelper.parseDate(offlineLogRequest
                .getOperateTime(), Constants.DATE_TIME_MS_FORMAT));
        offlineLog.setOperateType(offlineLogRequest.getOperateType());

        offlineLog.setWaybillCode(offlineLogRequest.getWaybillCode());
        offlineLog.setPackageCode(offlineLogRequest.getPackageCode());
        offlineLog.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
        offlineLog.setSealBoxCode(offlineLogRequest.getSealBoxCode());
        offlineLog.setSendCode(offlineLogRequest.getBatchCode());
        offlineLog.setSendUserCode(offlineLogRequest.getSendUserCode());
        offlineLog.setSendUser(offlineLogRequest.getSendUser());
        offlineLog.setShieldsCarCode(offlineLogRequest.getShieldsCarCode());
        offlineLog.setTaskType(offlineLogRequest.getTaskType());
        offlineLog.setVehicleCode(offlineLogRequest.getCarCode());
        offlineLog.setVolume(offlineLogRequest.getVolume());
        offlineLog.setWeight(offlineLogRequest.getWeight());
        offlineLog.setTurnoverBoxCode(offlineLogRequest.getTurnoverBoxCode());

        offlineLog.setStatus(status);

        return offlineLog;
    }


    /**
     * 将OfflineLogRequest转化为OperationLog（操作日志）
     *
     * @param offlineLogRequest
     * @return
     */
    private OperationLog requestConvertOperationLog(OfflineLogRequest offlineLogRequest) {
        OperationLog operationLog = new OperationLog();
        operationLog.setBoxCode(offlineLogRequest.getBoxCode());
        operationLog.setWaybillCode(offlineLogRequest.getWaybillCode());
        operationLog.setPackageCode(offlineLogRequest.getPackageCode());
        operationLog.setSendCode(offlineLogRequest.getBatchCode());
        operationLog.setCreateSiteCode(offlineLogRequest.getSiteCode());
        operationLog.setCreateSiteName(offlineLogRequest.getSiteName());
        operationLog.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
        operationLog.setCreateUser(offlineLogRequest.getUserName());
        operationLog.setCreateUserCode(offlineLogRequest.getUserCode());
        operationLog.setCreateTime(new Date());

        //因为后续的操作会根据操作时间冲掉这里的记录，所以这里将时间的long值减一，以确保不会被冲掉
        Date OperateTime = DateHelper.parseDate(offlineLogRequest.getOperateTime(), Constants.DATE_TIME_MS_FORMAT);
        OperateTime.setTime(OperateTime.getTime() - 1);
        operationLog.setOperateTime(OperateTime);

        operationLog.setLogType(OperationLog.LOG_TYPE_SEND_DELIVERY);
        operationLog.setRemark(OFFLINE_DELIVERY_REMARK);
        return operationLog;
    }
}
