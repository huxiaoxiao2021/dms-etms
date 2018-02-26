package com.jd.bluedragon.distribution.consumer.transport;

import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.transport.domain.ArAirFlightRealTimeStatus;
import com.jd.bluedragon.distribution.transport.domain.ArAirWaybillStatus;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 消费航班起飞降落实时MQ
 * <p>
 * <p>
 * Created by lixin39 on 2018/2/5.
 */
@Service("arAirFlightRealTimeConsumer")
public class ArAirFlightRealTimeConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private VosManager vosManager;

    @Autowired
    private ArSendRegisterService arSendRegisterService;

    @Autowired
    private ArSendCodeService arSendCodeService;

    @Autowired
    private SendDatailDao sendDetailDao;

    @Qualifier("arAirWaybillStatusMQ")
    @Autowired
    private DefaultJMQProducer arAirWaybillStatusMQ;

    @JProfiler(jKey = "DMSCORE.ArAirFlightRealTimeConsumer.consume", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("[空铁项目]消费航班起飞降落实时MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        ArAirFlightRealTimeStatus realTimeStatus = JsonHelper.fromJsonUseGson(message.getText(), ArAirFlightRealTimeStatus.class);
        ArSendRegister sendRegister = arSendRegisterService.getByFlightInfo(realTimeStatus.getFlightNumber(), realTimeStatus.getFilghtDate());
        if (sendRegister != null) {
            List<ArSendCode> sendCodes = arSendCodeService.getBySendRegisterId(sendRegister.getId());
            if (sendCodes != null && sendCodes.size() > 0) {
                for (ArSendCode sendCode : sendCodes) {
                    SealCarDto sealCarDto = null;
                    try {
                        sealCarDto = vosManager.querySealCarByBatchCode(sendCode.getSendCode());
                        this.buildAirWaybillAndSendMQ(sendCode.getSendCode(), realTimeStatus, sealCarDto);
                    } catch (Exception e) {
                        logger.error("[空铁项目]消费航班起飞降落实时MQ-批次号(" + sendCode.getSendCode() + ")-根据批次号封装运单维度消息体并发送给路由时发生异常", e);
                        SystemLogUtil.log(sendCode.getSendCode(), realTimeStatus.getFlightNumber(), sealCarDto == null ? "" : sealCarDto.getTransportCode(), sendRegister.getId().longValue(),
                                message.getText(), SystemLogContants.TYPE_AR_AIR_FLIGHT_REAL_TIME);
                        throw e;
                    }
                }
            } else {
                logger.warn("[空铁项目]消费航班起飞降落实时MQ-根据发货登记信息ID(" + sendRegister.getId() + ")获取批次号列表为空或null");
            }
        } else {
            logger.warn("[空铁项目]消费航班起飞降落实时MQ-根据航班号(" + realTimeStatus.getFlightNumber() + ")和飞行日期(" + DateHelper.formatDate(realTimeStatus.getFilghtDate()) + ")获取发货登记信息为null");
        }
    }

    /**
     * 构建运单维度航空起飞/降落信息并且发送MQ
     *
     * @param sendCode       批次号
     * @param realTimeStatus 实际航班飞行信息
     * @param sealCarDto     封车信息
     * @throws JMQException
     */
    private void buildAirWaybillAndSendMQ(String sendCode, ArAirFlightRealTimeStatus realTimeStatus, SealCarDto sealCarDto) throws JMQException {
        ArAirWaybillStatus airWaybillStatus = new ArAirWaybillStatus();
        /* 航班号 */
        airWaybillStatus.setFlightNumber(realTimeStatus.getFlightNumber());
        /* 起飞20 ,降落30 */
        airWaybillStatus.setStatus(realTimeStatus.getStatus());
        /* 实际时间 起飞/降落时间 */
        airWaybillStatus.setRealTime(realTimeStatus.getRealTime());
        /* 批次号 */
        airWaybillStatus.setBatchCode(sendCode);
        /* 始发机场编码 */
        airWaybillStatus.setBeginNodeCode(realTimeStatus.getBeginNodeCode());
        /* 始发机场名称 */
        airWaybillStatus.setBeginNodeName(realTimeStatus.getBeginNodeName());
        /* 目的机场编码 */
        airWaybillStatus.setEndNodeCode(realTimeStatus.getEndNodeCode());
        /* 目的机场名称 */
        airWaybillStatus.setEndNodeName(realTimeStatus.getEndNodeName());
        /* 预计起飞时间 */
        airWaybillStatus.setTakeOffTime(realTimeStatus.getTakeOffTime());
        /* 预计到达时间 */
        airWaybillStatus.setTouchDownTime(realTimeStatus.getTouchDownTime());
        /* 是否延误 */
        airWaybillStatus.setDelayFlag(realTimeStatus.getDelayFlag());
        if (sealCarDto != null) {
            /* 运力编码 */
            airWaybillStatus.setTransportCode(sealCarDto.getTransportCode());
            /* 发车条码 */
            airWaybillStatus.setSendCarCode(sealCarDto.getSealCarCode());
        } else {
            logger.warn("调用运输接口[vosQueryWS.querySealCarByBatchCode()]根据批次号(" + sendCode + ")获取封车信息为null");
        }

        String[] waybillArray = this.getWaybillBySendCode(sendCode);
        if (waybillArray != null && waybillArray.length > 0) {
            for (int i = 0, len = waybillArray.length; i < len; i++) {
                airWaybillStatus.setWayBillCode(waybillArray[i]);
                // 发送MQ消息
                this.sendMQ(airWaybillStatus);
            }
        }
    }

    /**
     * 根据批次号获取运单号(去重)
     *
     * @param sendCode
     * @return
     */
    private String[] getWaybillBySendCode(String sendCode) {
        List<SendDetail> sendDetailList = sendDetailDao.queryWaybillsBySendCode(sendCode);
        if (null != sendDetailList && sendDetailList.size() > 0) {
            Set<String> waybillSets = new HashSet<String>();
            for (SendDetail sendDetail : sendDetailList) {
                waybillSets.add(sendDetail.getWaybillCode());
            }
            return waybillSets.toArray(new String[waybillSets.size()]);
        } else {
            logger.warn("[空铁项目]消费航班起飞降落实时MQ-根据批次号获取发货明细为空，批次号：" + sendCode);
        }
        return null;
    }

    /**
     * 发送给路由MQ消息
     *
     * @param arAirWaybillStatus
     * @throws JMQException
     */
    private void sendMQ(ArAirWaybillStatus arAirWaybillStatus) throws JMQException {
        arAirWaybillStatusMQ.send(arAirWaybillStatus.getWayBillCode(), JsonHelper.toJson(arAirWaybillStatus));
        logger.info("[空铁项目]消费航班起飞降落实时MQ-发送运单维度消息成功，消息体：" + JsonHelper.toJson(arAirWaybillStatus));
    }

}
