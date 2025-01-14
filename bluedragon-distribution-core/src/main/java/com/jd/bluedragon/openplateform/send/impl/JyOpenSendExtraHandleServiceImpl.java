package com.jd.bluedragon.openplateform.send.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.constants.JyCacheKeyConstants;
import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;
import com.jd.bluedragon.distribution.open.entity.OperatorInfo;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.openplateform.send.JyOpenSendExtraHandleService;
import com.jd.bluedragon.openplateform.send.dto.JyTysSendFinishDto;
import com.jd.bluedragon.openplateform.send.dto.JyTysSendPackageDetailDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.constants.JyConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发货后其他处理动作
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-26 17:41:03 周五
 */
@Service
public class JyOpenSendExtraHandleServiceImpl implements JyOpenSendExtraHandleService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("loadPackageSendCompleteProducer")
    private DefaultJMQProducer loadPackageSendCompleteProducer;

    @Autowired
    @Qualifier("transportSendPackageProducer")
    private DefaultJMQProducer transportSendPackageProducer;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 发货后处理城配相关
     * @return 处理结果
     * @author fanggang7
     * @time 2023-05-26 17:42:19 周五
     */
    @Override
    @JProfiler(jKey = "JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSend",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> afterOpenPlatformSend(JYCargoOperateEntity jyCargoOperate){
        if(log.isInfoEnabled()) {
            log.info("JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSend param {}", JSON.toJSONString(jyCargoOperate));
        }
        Result<Boolean> result = Result.success();
        try {
            // 发出转运发货完成后的两个mq消息
            this.sendTysSendMq4Urban(jyCargoOperate);
        } catch (Exception e) {
            log.error("JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSend exception param {}", JSON.toJSONString(jyCargoOperate), e);
            return result.toFail("系统异常");
        }
        return result;
    }

    private String getJyOpenPlatformSendTaskCompleteCacheKey(String batchCode) {
        return String.format(JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY, batchCode);
    }

    /**
     * 发出城配发货明细消息
     */
    private void sendTysSendMq4Urban(JYCargoOperateEntity jyCargoOperate) throws JMQException {
        final OperatorInfo operatorInfo = jyCargoOperate.getOperatorInfo();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(jyCargoOperate.getCreateSiteId());
        currentOperate.setSiteName(jyCargoOperate.getCreateSiteName());
        currentOperate.setOperateTime(new Date(operatorInfo.getOperateTime()));

        User user = new User();
        user.setUserCode(JyConstants.JY_DEPPON_OPERATOR_ID);
        user.setUserErp(JyConstants.JY_DEPPON_OPERATOR_ERP);
        user.setUserName(operatorInfo.getOperateUserName());

        String jyOpenPlatformSendTaskCompleteLockKey = this.getJyOpenPlatformSendTaskCompleteCacheKey(jyCargoOperate.getSendCode());
        final String existSendCodeVal = redisClientOfJy.get(jyOpenPlatformSendTaskCompleteLockKey);
        log.info("sendTysSendMq4Urban jyOpenPlatformSendTaskCompleteLockKey: {} existSendCodeVal {}", jyOpenPlatformSendTaskCompleteLockKey, existSendCodeVal);
        if(existSendCodeVal == null){
            if (jyCargoOperate.getTaskScanBeginTime() == null || jyCargoOperate.getTaskScanEndTime() == null) {
                log.warn("sendSendFinishMq4Urban taskScanBeginTime and taskScanEndTime is illegal {}", JsonHelper.toJson(jyCargoOperate));
                return;
            }
            if (jyCargoOperate.getTaskScanBeginTime() != null && jyCargoOperate.getTaskScanBeginTime() <= 0) {
                log.warn("sendTysSendMq4Urban taskScanBeginTime is illegal {}", JsonHelper.toJson(jyCargoOperate));
                return;
            }
            if (jyCargoOperate.getTaskScanEndTime() != null && jyCargoOperate.getTaskScanEndTime() <= 0) {
                log.warn("sendTysSendMq4Urban taskScanEndTime is illegal {}", JsonHelper.toJson(jyCargoOperate));
                return;
            }
            this.sendTaskCompleteMq(jyCargoOperate, currentOperate, user, jyOpenPlatformSendTaskCompleteLockKey);
        } else {
            if(jyCargoOperate.getTaskScanBeginTime() == null || jyCargoOperate.getTaskScanEndTime() == null){
                final String[] taskTimeArr = existSendCodeVal.split("-");
                if (taskTimeArr.length < 2) {
                    return;
                }
                if(jyCargoOperate.getTaskScanBeginTime() == null){
                    jyCargoOperate.setTaskScanBeginTime(Long.valueOf(taskTimeArr[0]));
                }
                if(jyCargoOperate.getTaskScanEndTime() == null){
                    jyCargoOperate.setTaskScanEndTime(Long.valueOf(taskTimeArr[1]));
                }
            }
        }

        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(jyCargoOperate.getBarcode());
        List<Message> sendDetailMQList = new ArrayList<>();
        if(BarCodeType.BOX_CODE.equals(barCodeType)){
            final String sendCode = jyCargoOperate.getSendCode();
            final List<SendDetail> boxDetailList = deliveryService.getSendDetailByBoxAndCreateAndReceiveSiteCode(jyCargoOperate.getBarcode(), BusinessUtil.getCreateSiteCodeFromSendCode(sendCode), BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode));
            if(CollectionUtils.isNotEmpty(boxDetailList)){
                for (SendDetail sendDetail : boxDetailList) {
                    final JyTysSendPackageDetailDto jyTysSendPackageDetailDto = this.genJyTysSendPackageDetailDto(jyCargoOperate, sendDetail.getPackageBarcode(), currentOperate, user);
                    log.info("sendTysSendMq4Urban transportSendPackageProducer topic: {} send {}", transportSendPackageProducer.getTopic(), JSON.toJSONString(jyTysSendPackageDetailDto));
                    sendDetailMQList.add(this.genMessage4JyTysSendPackageDetailDto(jyTysSendPackageDetailDto));
                }
            }
        } else {
            final JyTysSendPackageDetailDto jyTysSendPackageDetailDto = this.genJyTysSendPackageDetailDto(jyCargoOperate, jyCargoOperate.getBarcode(), currentOperate, user);
            sendDetailMQList.add(this.genMessage4JyTysSendPackageDetailDto(jyTysSendPackageDetailDto));
            log.info("sendTysSendMq4Urban transportSendPackageProducer topic: {} send {}", transportSendPackageProducer.getTopic(), JSON.toJSONString(jyTysSendPackageDetailDto));
        }
        transportSendPackageProducer.batchSendOnFailPersistent(sendDetailMQList);
    }

    private JyTysSendPackageDetailDto genJyTysSendPackageDetailDto(JYCargoOperateEntity jyCargoOperate, String packageCode, CurrentOperate currentOperate, User user) {
        final String sendCode = jyCargoOperate.getSendCode();
        final Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(sendCode);
        final Integer receiveSiteCode = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);

        final JyTysSendPackageDetailDto jyTysSendPackageDetailDto = new JyTysSendPackageDetailDto();
        jyTysSendPackageDetailDto.setSendCode(jyCargoOperate.getSendCode());
        jyTysSendPackageDetailDto.setReceiveSiteCode(receiveSiteCode);
        jyTysSendPackageDetailDto.setCurrentOperate(currentOperate);
        jyTysSendPackageDetailDto.setPackageCode(packageCode);
        jyTysSendPackageDetailDto.setUpdateTime(currentOperate.getOperateTime());
        jyTysSendPackageDetailDto.setUserCode(user.getUserCode());
        jyTysSendPackageDetailDto.setUserName(user.getUserName());
        jyTysSendPackageDetailDto.setUser(user);
        jyTysSendPackageDetailDto.setTaskFinishTime(new Date(jyCargoOperate.getTaskScanEndTime()));
        jyTysSendPackageDetailDto.setCreateSiteCode(createSiteCode);
        return jyTysSendPackageDetailDto;
    }

    private void sendTaskCompleteMq(JYCargoOperateEntity jyCargoOperate, CurrentOperate currentOperate, User user, String jyOpenPlatformSendTaskCompleteLockKey) throws JMQException {
        JyTysSendFinishDto jyTysSendFinishDto = this.genJyTysSendFinishDto(jyCargoOperate, currentOperate, user);
        log.info("sendTysSendMq4Urban loadPackageSendCompleteProducer topic: {} send {}", loadPackageSendCompleteProducer.getTopic(), JSON.toJSONString(jyTysSendFinishDto));
        loadPackageSendCompleteProducer.send(jyTysSendFinishDto.getSendCode(), JsonHelper.toJson(jyTysSendFinishDto));
        // 设置缓存
        redisClientOfJy.setEx(jyOpenPlatformSendTaskCompleteLockKey, String.format("%s-%s", jyCargoOperate.getTaskScanBeginTime(), jyCargoOperate.getTaskScanEndTime()),
                JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY_EXPIRED, JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY_EXPIRED_TIME_UNIT);
    }

    private JyTysSendFinishDto genJyTysSendFinishDto(JYCargoOperateEntity jyCargoOperate, CurrentOperate currentOperate, User user){
        final String sendCode = jyCargoOperate.getSendCode();
        final Integer receiveSiteCode = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);

        final JyTysSendFinishDto jyTysSendFinishDto = new JyTysSendFinishDto();
        jyTysSendFinishDto.setSendCode(jyCargoOperate.getSendCode());
        jyTysSendFinishDto.setCurrentOperate(currentOperate);
        jyTysSendFinishDto.setUser(user);
        jyTysSendFinishDto.setTaskFinishTime(new Date(jyCargoOperate.getTaskScanEndTime()));
        jyTysSendFinishDto.setPackageSendCompleteTime(jyCargoOperate.getTaskScanEndTime());
        jyTysSendFinishDto.setReceiveSiteCode(receiveSiteCode);
        return jyTysSendFinishDto;
    }

    private Message genMessage4JyTysSendPackageDetailDto(JyTysSendPackageDetailDto jyTysSendPackageDetailDto) {
        Message message = new Message();
        message.setTopic(transportSendPackageProducer.getTopic());
        message.setText(JSON.toJSONString(jyTysSendPackageDetailDto));
        message.setBusinessId(jyTysSendPackageDetailDto.getPackageCode());
        return message;
    }

    /**
     * 发货完成后处理
     *
     * @return 处理结果
     * @author fanggang7
     * @time 2023-06-08 10:09:32 周四
     */
    @Override
    @JProfiler(jKey = "JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSendFinish",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Boolean> afterOpenPlatformSendFinish(JYCargoOperateEntity jyCargoOperate) {
        if(log.isInfoEnabled()){
            log.info("JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSendFinish param {}", JSON.toJSONString(jyCargoOperate));
        }
        Result<Boolean> result = Result.success();
        try {
            // 发出转运发货完成后的两个mq消息
            this.sendSendFinishMq4Urban(jyCargoOperate);
        } catch (Exception e) {
            log.error("JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSendFinish exception param {}", JSON.toJSONString(jyCargoOperate), e);
            return result.toFail("系统异常");
        }
        return result;
    }

    private void sendSendFinishMq4Urban(JYCargoOperateEntity jyCargoOperate) throws JMQException {
        if (jyCargoOperate.getTaskScanBeginTime() == null || jyCargoOperate.getTaskScanEndTime() == null) {
            log.warn("sendSendFinishMq4Urban taskScanBeginTime and taskScanEndTime is illegal {}", JsonHelper.toJson(jyCargoOperate));
            return;
        }
        if (jyCargoOperate.getTaskScanBeginTime() != null && jyCargoOperate.getTaskScanBeginTime() <= 0) {
            log.warn("sendSendFinishMq4Urban taskScanBeginTime is illegal {}", JsonHelper.toJson(jyCargoOperate));
            return;
        }
        if (jyCargoOperate.getTaskScanEndTime() != null && jyCargoOperate.getTaskScanEndTime() <= 0) {
            log.warn("sendSendFinishMq4Urban taskScanEndTime is illegal {}", JsonHelper.toJson(jyCargoOperate));
            return;
        }
        final OperatorInfo operatorInfo = jyCargoOperate.getOperatorInfo();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(jyCargoOperate.getCreateSiteId());
        currentOperate.setSiteName(jyCargoOperate.getCreateSiteName());
        currentOperate.setOperateTime(new Date(operatorInfo.getOperateTime()));

        User user = new User();
        user.setUserCode(JyConstants.JY_DEPPON_OPERATOR_ID);
        user.setUserErp(JyConstants.JY_DEPPON_OPERATOR_ERP);
        user.setUserName(operatorInfo.getOperateUserName());

        // 发送装车完成消息
        String jyOpenPlatformSendTaskCompleteLockKey = this.getJyOpenPlatformSendTaskCompleteCacheKey(jyCargoOperate.getSendCode());
        final String existSendCodeVal = redisClientOfJy.get(jyOpenPlatformSendTaskCompleteLockKey);
        log.info("sendSendFinishMq4Urban jyOpenPlatformSendTaskCompleteLockKey: {} existSendCodeVal {}", jyOpenPlatformSendTaskCompleteLockKey, existSendCodeVal);
        if(existSendCodeVal == null){
            // 发送完成消息
            this.sendTaskCompleteMq(jyCargoOperate, currentOperate, user, jyOpenPlatformSendTaskCompleteLockKey);
        }

        // 查找发货明细，发送明细消息
        final Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(jyCargoOperate.getSendCode());
        SendDetailDto sendDetailDto = new SendDetailDto();
        sendDetailDto.setSendCode(jyCargoOperate.getSendCode());
        sendDetailDto.setIsCancel(0);
        sendDetailDto.setCreateSiteCode(createSiteCode);
        final List<String> packageCodeList = sendDetailService.queryPackageCodeBySendCode(sendDetailDto);
        if(log.isInfoEnabled()){
            log.info("sendSendFinishMq4Urban packageCodeList {}", JsonHelper.toJson(packageCodeList));
        }
        // 进一步分批，批量发送mq
        int batchSize = 50;
        int batchCount = Double.valueOf(Math.ceil(packageCodeList.size() / (double) batchSize)).intValue();
        log.info("sendSendFinishMq4Urban batchCount {}", batchCount);
        for (int i = 0; i < batchCount; i++) {
            int start = i * batchSize;
            int end = start + batchSize;
            if (end > packageCodeList.size()) {
                end = packageCodeList.size();
            }
            final List<String> packageCodeSubList = packageCodeList.subList(start, end);
            List<Message> sendDetailMQList = new ArrayList<>();
            for (String packageCode : packageCodeSubList) {
                final JyTysSendPackageDetailDto jyTysSendPackageDetailDto = this.genJyTysSendPackageDetailDto(jyCargoOperate, packageCode, currentOperate, user);
                log.info("sendSendFinishMq4Urban transportSendPackageProducer topic: {} send {}", transportSendPackageProducer.getTopic(), JSON.toJSONString(jyTysSendPackageDetailDto));
                sendDetailMQList.add(this.genMessage4JyTysSendPackageDetailDto(jyTysSendPackageDetailDto));
            }
            transportSendPackageProducer.batchSendOnFailPersistent(sendDetailMQList);
        }

        // 设置缓存
        redisClientOfJy.setEx(jyOpenPlatformSendTaskCompleteLockKey, String.format("%s-%s", jyCargoOperate.getTaskScanBeginTime(), jyCargoOperate.getTaskScanEndTime()),
                JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY_EXPIRED, JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY_EXPIRED_TIME_UNIT);

    }
}
