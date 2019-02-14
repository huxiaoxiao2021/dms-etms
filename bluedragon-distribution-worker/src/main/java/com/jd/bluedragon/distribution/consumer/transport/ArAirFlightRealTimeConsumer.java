package com.jd.bluedragon.distribution.consumer.transport;

import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.domain.*;
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
import java.util.*;

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

    @Autowired
    @Qualifier("arSendRegisterDao")
    private ArSendRegisterDao arSendRegisterDao;

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

        /**
         * 把发给路由MQ标识落到arSendRegister表
         */
        List<ArSendRegister> sendRegisterList = arSendRegisterService.getAirListByTransParam(realTimeStatus.getFlightNumber(), realTimeStatus.getFlightDate());
        //只有起飞才落字段
        if (realTimeStatus != null && realTimeStatus.getStatus() == 20) {
            //查出当天该航班号起飞的发货登记记录
            if (sendRegisterList != null && !sendRegisterList.isEmpty()) {
                for(ArSendRegister arSendRegister:sendRegisterList){
                    arSendRegister.setSendRouterMqType(ArSendRouterMqTypeEnum.AIR_ALREADY_SEND.getCode());
                    //把字段落库
                    arSendRegisterDao.update(arSendRegister);
                }
            }
        }

        if (sendRegisterList != null && !sendRegisterList.isEmpty()) {
            List<Long> sendRegisterIds = getRegisterIdList(sendRegisterList);
            List<String> sendCodes = this.getSendCodes(sendRegisterIds);
            if (!sendCodes.isEmpty()) {
                for (String sendCode : sendCodes) {
                    SealCarDto sealCarDto = null;
                    try {
                        sealCarDto = vosManager.querySealCarByBatchCode(sendCode);
                        this.buildAirWaybillAndSendMQ(sendCode, realTimeStatus, sealCarDto);
                    } catch (Exception e) {
                        logger.error("[空铁项目]消费航班起飞降落实时MQ-批次号(" + sendCode + ")-根据批次号封装运单维度消息体并发送给路由时发生异常", e);
                        SystemLogUtil.log(sendCode, realTimeStatus.getFlightNumber(), sealCarDto == null ? "" : sealCarDto.getTransportCode(), null,
                                message.getText(), SystemLogContants.TYPE_AR_AIR_FLIGHT_REAL_TIME);
                        throw e;
                    }
                }
            } else {
                logger.warn("[空铁项目]消费航班起飞降落实时MQ-根据发货登记信息ID(" + sendRegisterIds.toString() + ")获取批次号列表为空或null");
            }
        } else {
            logger.warn("[空铁项目]消费航班起飞降落实时MQ-根据航班号(" + realTimeStatus.getFlightNumber() + ")和飞行日期(" + DateHelper.formatDate(realTimeStatus.getFlightDate()) + ")获取发货登记信息为null");
        }
    }

    private List<Long> getRegisterIdList(List<ArSendRegister> sendRegisterList) {
        List<Long> ids = new ArrayList<Long>(sendRegisterList.size());
        for (ArSendRegister arSendRegister : sendRegisterList) {
            ids.add(arSendRegister.getId());
        }
        return ids;
    }

    private List<String> getSendCodes(List<Long> sendRegisterIds) {
        Set<String> sendCodes = new HashSet<String>();
        List<ArSendCode> arSendCodeList = arSendCodeService.getBySendRegisterIds(sendRegisterIds);
        if (arSendCodeList != null && arSendCodeList.size() > 0) {
            for (ArSendCode arSendCode : arSendCodeList) {
                sendCodes.add(arSendCode.getSendCode());
            }
        }
        return new ArrayList<String>(sendCodes);
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
            /* 路由线路编码 */
            airWaybillStatus.setRouteLineCode(sealCarDto.getRouteLineCode());
        } else {
            logger.warn("调用运输接口[vosQueryWS.querySealCarByBatchCode()]根据批次号(" + sendCode + ")获取封车信息为null");
        }
        this.doSendMQ(sendCode, airWaybillStatus);
    }

    /**
     * 根据批次号获取发货明细信息，组装实体发送包裹维度的MQ消息
     *
     * @param sendCode
     * @return
     */
    private void doSendMQ(String sendCode, ArAirWaybillStatus airWaybillStatus) throws JMQException {
        List<SendDetail> sendDetailList = sendDetailDao.queryWaybillsBySendCode(sendCode);
        if (null != sendDetailList && sendDetailList.size() > 0) {
            for (SendDetail sendDetail : sendDetailList) {
                /* 运单号 */
                airWaybillStatus.setWayBillCode(sendDetail.getWaybillCode());
                /* 包裹号 */
                airWaybillStatus.setPackageCode(sendDetail.getPackageBarcode());
                // 发送MQ消息
                this.sendMQ(airWaybillStatus);
            }
        } else {
            logger.warn("[空铁项目]消费航班起飞降落实时MQ-根据批次号获取发货明细为空，批次号：" + sendCode);
        }
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
