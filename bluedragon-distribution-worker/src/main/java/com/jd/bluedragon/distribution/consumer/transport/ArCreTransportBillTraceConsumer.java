package com.jd.bluedragon.distribution.consumer.transport;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.transport.domain.ArCreTransportBillTrace;
import com.jd.bluedragon.distribution.transport.domain.ArRailwayTransportWaybillStatus;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lixin39
 * @Description TMS铁路主运单号、实际发车/到达时间MQ消费逻辑
 * @ClassName ArCreTransportBillTraceConsumer
 * @date 2019/1/21
 */
@Service("arCreTransportBillTraceConsumer")
public class ArCreTransportBillTraceConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArSendRegisterService arSendRegisterService;

    @Autowired
    private ArSendCodeService arSendCodeService;

    @Autowired
    private SendDatailDao sendDetailDao;

    @Autowired
    private VosManager vosManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Qualifier("arRailwayWaybillStatusMQ")
    @Autowired
    private DefaultJMQProducer arRailwayWaybillStatusMQ;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[空铁项目]TMS铁路主运单号、实际发车/到达时间MQ消费-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        try {
            ArCreTransportBillTrace creTransportBillTrace = JsonHelper.fromJsonUseGson(message.getText(), ArCreTransportBillTrace.class);
            this.doConsume(creTransportBillTrace);
        } catch (Exception e) {
            log.error("[空铁项目]消费TMS铁路运输订单信息MQ时发生异常,内容为【{}】", message.getText(), e);
            throw new RuntimeException(e);
        }
    }

    private void doConsume(ArCreTransportBillTrace creTransportBillTrace) throws Exception {
        String creTransBillCode = creTransportBillTrace.getCreTransbillCode();
        if (StringUtils.isEmpty(creTransBillCode)) {
            log.warn("[空铁项目]消费TMS铁路运输订单信息MQ-中铁运单号为null");
            return;
        }
        List<ArSendRegister> sendRegisterList = arSendRegisterService.getRailwayListByTransParam(creTransBillCode);
        if (!sendRegisterList.isEmpty()) {
            List<Long> sendRegisterIds = getRegisterIdList(sendRegisterList);
            List<String> sendCodes = this.getSendCodes(sendRegisterIds);
            if (!sendCodes.isEmpty()) {
                for (String sendCode : sendCodes) {
                    SealCarDto sealCarDto = vosManager.querySealCarByBatchCode(sendCode);
                    this.buildRailwayWaybillAndSendMQ(sendCode, creTransportBillTrace, sealCarDto);
                }
            } else {
                log.warn("[空铁项目]消费TMS铁路运输订单信息MQ-根据发货登记信息ID({})获取批次号列表为空或null",sendRegisterIds.toString() );
            }
        } else {
            log.warn("[空铁项目]消费TMS铁路运输订单信息MQ-根据中铁运单号({})获取发货登记信息为null",creTransBillCode);
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
        if (arSendCodeList.size() > 0) {
            for (ArSendCode arSendCode : arSendCodeList) {
                sendCodes.add(arSendCode.getSendCode());
            }
        }
        return new ArrayList<String>(sendCodes);
    }

    /**
     * 构建运单维度航空起飞/降落信息并且发送MQ
     *
     * @param sendCode              批次号
     * @param creTransportBillTrace 实际航班飞行信息
     * @param sealCarDto            封车信息
     * @throws JMQException
     */
    private void buildRailwayWaybillAndSendMQ(String sendCode, ArCreTransportBillTrace creTransportBillTrace, SealCarDto sealCarDto) throws JMQException {
        ArRailwayTransportWaybillStatus railwayWaybillStatus = new ArRailwayTransportWaybillStatus();
        /* 航班号 */
        railwayWaybillStatus.setCreTransbillCode(creTransportBillTrace.getCreTransbillCode().trim().toUpperCase());
        if (creTransportBillTrace.getTrainNumber() != null) {
            /* 铁路车次号 */
            railwayWaybillStatus.setTrainNumber(creTransportBillTrace.getTrainNumber().toUpperCase());
        }
        /* 铁路类型：1 - 高铁，2 - 行包 */
        railwayWaybillStatus.setRailwayType(creTransportBillTrace.getRailwayType());
        /* 节点名称：0 承运制票、20 装车确认、30 卸车确认、40 提货交付 */
        railwayWaybillStatus.setStatus(creTransportBillTrace.getStatus());
        if (creTransportBillTrace.getOperateTime() != null) {
            /* 实际时间 起飞/降落时间 */
            railwayWaybillStatus.setOperateTime(new Date(creTransportBillTrace.getOperateTime()));
        }
        /* 批次号 */
        railwayWaybillStatus.setBatchCode(sendCode);
        /* 出发城市 */
        railwayWaybillStatus.setBeginCityName(creTransportBillTrace.getBeginCityName());
        /* 始发机场名称 */
        railwayWaybillStatus.setBeginStationName(creTransportBillTrace.getBeginStationName());
        /* 目的城市 */
        railwayWaybillStatus.setEndCityName(creTransportBillTrace.getEndCityName());
        /* 目的机场名称 */
        railwayWaybillStatus.setEndStationName(creTransportBillTrace.getEndStationName());
        if (sealCarDto != null) {
            /* 运力编码 */
            railwayWaybillStatus.setTransportCode(sealCarDto.getTransportCode());
            /* 发车条码 */
            railwayWaybillStatus.setSendCarCode(sealCarDto.getSealCarCode());
            /* 路由线路编码 */
            railwayWaybillStatus.setRouteLineCode(sealCarDto.getRouteLineCode());
        } else {
            log.warn("调用运输接口[vosQueryWS.querySealCarByBatchCode()]根据批次号({})获取封车信息为null",sendCode);
        }
        this.doSendMQ(sendCode, railwayWaybillStatus);
    }

    /**
     * 根据批次号获取发货明细信息，组装实体发送包裹维度的MQ消息
     *
     * @param sendCode
     * @return
     */
    private void doSendMQ(String sendCode, ArRailwayTransportWaybillStatus railwayWaybillStatus) throws JMQException {
        List<SendDetail> sendDetailList = sendDetailDao.querySendDetailBySendCode(sendCode);
        if (sendDetailList.size() > 0) {
            this.buildCityInfoBySiteCode(railwayWaybillStatus, sendDetailList.get(0).getCreateSiteCode(), sendDetailList.get(0).getReceiveSiteCode());
            for (SendDetail sendDetail : sendDetailList) {
                /* 运单号 */
                railwayWaybillStatus.setWayBillCode(sendDetail.getWaybillCode());
                /* 包裹号 */
                railwayWaybillStatus.setPackageCode(sendDetail.getPackageBarcode());
                // 发送给路由MQ消息
                arRailwayWaybillStatusMQ.send(railwayWaybillStatus.getWayBillCode(), JsonHelper.toJson(railwayWaybillStatus));
            }
        } else {
            log.warn("[空铁项目]消费航班起飞降落实时MQ-根据批次号获取发货明细为空，批次号：{}" , sendCode);
        }
    }

    /**
     * 根据发货的 始发和目的站点信息构建城市信息
     *
     * @param railwayWaybillStatus
     * @param createSiteCode
     * @param receiveSiteCode
     */
    private void buildCityInfoBySiteCode(ArRailwayTransportWaybillStatus railwayWaybillStatus, Integer createSiteCode, Integer receiveSiteCode) {
        BaseStaffSiteOrgDto createSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
        if (createSiteOrgDto != null) {
            railwayWaybillStatus.setBeginCityCode(createSiteOrgDto.getCityId());
            railwayWaybillStatus.setBeginCityName(createSiteOrgDto.getCityName());
        }
        BaseStaffSiteOrgDto receiveSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        if (receiveSiteOrgDto != null) {
            railwayWaybillStatus.setEndCityCode(receiveSiteOrgDto.getCityId());
            railwayWaybillStatus.setEndCityName(receiveSiteOrgDto.getCityName());
        }
    }

}
