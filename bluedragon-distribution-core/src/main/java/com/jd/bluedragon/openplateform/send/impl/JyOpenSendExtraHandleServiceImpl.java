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
import com.jd.bluedragon.distribution.send.service.DeliveryService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private SendDatailDao sendDatailDao;

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 发货后处理城配相关
     * @return 处理结果
     * @author fanggang7
     * @time 2023-05-26 17:42:19 周五
     */
    @Override
    public Result<Boolean> afterOpenPlatformSend(JYCargoOperateEntity jyCargoOperate){
        log.info("JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSend param {}", JSON.toJSONString(jyCargoOperate));
        Result<Boolean> result = Result.success();
        try {
            // 发出转运发货完成后的两个mq消息
            this.sendTysSendMq4Urban(jyCargoOperate);
        } catch (Exception e) {
            log.info("JyOpenSendExtraHandleServiceImpl.afterOpenPlatformSend exception param {}", JSON.toJSONString(jyCargoOperate), e);
            return result.toFail("系统异常");
        }
        return result;
    }

    private String getJyOpenPlatformSendTaskCompleteLockKey(String batchCode) {
        return String.format(JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_LOCK, batchCode);
    }

    private String getJyOpenPlatformSendTaskCompleteCacheKey(String batchCode) {
        return String.format(JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY, batchCode);
    }

    /**
     * 发出城配发货明细消息
     */
    private void sendTysSendMq4Urban(JYCargoOperateEntity jyCargoOperate) throws JMQException {
        final String sendCode = jyCargoOperate.getSendCode();
        final Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(sendCode);
        final Integer receiveSiteCode = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);
        final OperatorInfo operatorInfo = jyCargoOperate.getOperatorInfo();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(jyCargoOperate.getCreateSiteId());
        currentOperate.setSiteName(jyCargoOperate.getCreateSiteName());
        currentOperate.setOperateTime(new Date(operatorInfo.getOperateTime()));

        User user = new User();
        user.setUserCode(JyConstants.JY_DEPPON_OPERATOR_ID);
        user.setUserErp(JyConstants.JY_DEPPON_OPERATOR_ERP);
        user.setUserName(operatorInfo.getOperateUserName());

        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(jyCargoOperate.getBarcode());
        List<Message> sendDetailMQList = new ArrayList<>();
        if(BarCodeType.BOX_CODE.equals(barCodeType)){
            final List<SendDetail> boxDetailList = deliveryService.getCancelSendByBox(jyCargoOperate.getBarcode());
            for (SendDetail sendDetail : boxDetailList) {
                final JyTysSendPackageDetailDto jyTysSendPackageDetailDto = new JyTysSendPackageDetailDto();
                jyTysSendPackageDetailDto.setSendCode(sendCode);
                jyTysSendPackageDetailDto.setReceiveSiteCode(receiveSiteCode);
                jyTysSendPackageDetailDto.setCurrentOperate(currentOperate);
                jyTysSendPackageDetailDto.setPackageCode(sendDetail.getPackageBarcode());
                jyTysSendPackageDetailDto.setUpdateTime(currentOperate.getOperateTime());
                jyTysSendPackageDetailDto.setUserCode(user.getUserCode());
                jyTysSendPackageDetailDto.setUserName(operatorInfo.getOperateUserName());
                jyTysSendPackageDetailDto.setUser(user);
                jyTysSendPackageDetailDto.setTaskFinishTime(new Date(jyCargoOperate.getTaskScanEndTime()));
                jyTysSendPackageDetailDto.setCreateSiteCode(createSiteCode);
                sendDetailMQList.add(this.genMessage4JyTysSendPackageDetailDto(jyTysSendPackageDetailDto));
            }
        } else {
            final JyTysSendPackageDetailDto jyTysSendPackageDetailDto = new JyTysSendPackageDetailDto();
            jyTysSendPackageDetailDto.setSendCode(sendCode);
            jyTysSendPackageDetailDto.setReceiveSiteCode(receiveSiteCode);
            jyTysSendPackageDetailDto.setCurrentOperate(currentOperate);
            jyTysSendPackageDetailDto.setPackageCode(jyCargoOperate.getBarcode());
            jyTysSendPackageDetailDto.setUpdateTime(currentOperate.getOperateTime());
            jyTysSendPackageDetailDto.setUserCode(user.getUserCode());
            jyTysSendPackageDetailDto.setUserName(operatorInfo.getOperateUserName());
            jyTysSendPackageDetailDto.setUser(user);
            jyTysSendPackageDetailDto.setTaskFinishTime(new Date(jyCargoOperate.getTaskScanEndTime()));
            jyTysSendPackageDetailDto.setCreateSiteCode(createSiteCode);
            sendDetailMQList.add(this.genMessage4JyTysSendPackageDetailDto(jyTysSendPackageDetailDto));
        }
        log.info("sendTysSendMq4Urban transportSendPackageProducer topic: {} send {}", transportSendPackageProducer.getTopic(), JSON.toJSONString(sendDetailMQList));
        transportSendPackageProducer.batchSendOnFailPersistent(sendDetailMQList);

        String jyOpenPlatformSendTaskCompleteLockKey = this.getJyOpenPlatformSendTaskCompleteCacheKey(sendCode);
        final String existSendCodeVal = redisClientOfJy.get(jyOpenPlatformSendTaskCompleteLockKey);
        if(existSendCodeVal == null){
            // 查询send_d是否已有同批次已发货数据
            final Integer sendExistCount = sendDatailDao.querySendDCountBySendCode(jyCargoOperate.getSendCode());
            if(sendExistCount <= 0){
                final JyTysSendFinishDto jyTysSendFinishDto = new JyTysSendFinishDto();
                jyTysSendFinishDto.setSendCode(jyTysSendFinishDto.getSendCode());
                jyTysSendFinishDto.setCurrentOperate(currentOperate);
                jyTysSendFinishDto.setUser(user);
                jyTysSendFinishDto.setTaskFinishTime(new Date(jyCargoOperate.getTaskScanEndTime()));
                jyTysSendFinishDto.setPackageSendCompleteTime(jyCargoOperate.getTaskScanEndTime());
                jyTysSendFinishDto.setReceiveSiteCode(receiveSiteCode);
                log.info("sendTysSendMq4Urban loadPackageSendCompleteProducer topic: {} send {}", loadPackageSendCompleteProducer.getTopic(), JSON.toJSONString(sendDetailMQList));
                loadPackageSendCompleteProducer.send(jyTysSendFinishDto.getSendCode(), JsonHelper.toJson(jyTysSendFinishDto));
                // 设置缓存
                redisClientOfJy.setEx(jyOpenPlatformSendTaskCompleteLockKey, Constants.YN_YES.toString(),
                        JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY_EXPIRED, JyCacheKeyConstants.JY_OPEN_PLATFORM_SEND_TASK_COMPLETE_KEY_EXPIRED_TIME_UNIT);
            }
        }
    }

    private Message genMessage4JyTysSendPackageDetailDto(JyTysSendPackageDetailDto jyTysSendPackageDetailDto) {
        Message message = new Message();
        message.setTopic(transportSendPackageProducer.getTopic());
        message.setText(JSON.toJSONString(jyTysSendPackageDetailDto));
        message.setBusinessId(jyTysSendPackageDetailDto.getPackageCode());
        return message;
    }
}
