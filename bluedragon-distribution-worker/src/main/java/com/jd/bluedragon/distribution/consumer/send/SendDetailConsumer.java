package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.distribution.send.domain.SendDispatchDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSON;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 发货明细MQ[dmsWorkSendDetail] 消费逻辑
 *
 * @author lixin39
 */
@Service("sendDetailConsumer")
public class SendDetailConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(SendDetailConsumer.class);

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private InspectionService inspectionService;

    @Qualifier("pop1MQ")
    @Autowired
    private DefaultJMQProducer pop1MQ;

    @Autowired
    @Qualifier("dmsToVendor")
    private DefaultJMQProducer dmsToVendor;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    /**
     * 缓存redis的key
     */
    private final static String REDIS_CACHE_KEY = "MQ-CONSUMER-SEND-DETAIL-";

    /**
     * 缓存redis的到期时间，5s
     */
    private final static long REDIS_CACHE_EXPIRE_TIME = 5;

    /**
     * 分隔符号
     */
    private final static String SEPARATOR = "-";

    @Override
    public void consume(Message message) {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("[dmsWorkSendDetail消费]MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        SendDetailMessage context = JsonHelper.fromJsonUseGson(message.getText(), SendDetailMessage.class);
        boolean isLock = false;
        try {
            Long operateTime = context.getOperateTime();
            if (StringUtils.isNotEmpty(context.getPackageBarcode()) && operateTime != null && operateTime > 0) {
                // 设置redis缓存锁
                String lastOperateTime = this.setCacheLock(context);
                if (lastOperateTime == null) {
                    isLock = true;
                    // 上次操作时间为null，表示缓存锁加入成功，执行消费逻辑
                    this.doConsume(context);
                } else {
                    // 根据发货操作时间判断数据是否有效
                    if (operateTime > Long.valueOf(lastOperateTime)) {
                        throw new RuntimeException("[dmsWorkSendDetail消费]该任务已被锁定，抛出异常进行重试，MQ message body:" + message.getText());
                    } else {
                        // 无效
                        logger.warn("[dmsWorkSendDetail消费]无效发货明细消息，重复操作，MQ message body:" + message.getText());
                        return;
                    }
                }
            } else {
                logger.warn("[dmsWorkSendDetail消费]无效发货明细消息，包裹号或者操作时间错误，MQ message body:" + message.getText());
            }
        } catch (Exception e) {
            logger.error("[dmsWorkSendDetail消费]消费异常" + "，MQ message body:" + message.getText(), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + message.getText(), e);
        } finally {
            if (isLock) {
                // 删除redis缓存锁
                this.delCacheLock(context);
            }
        }
    }

    /**
     * Redis缓存锁，解决相同包裹在同一站点同事操作并发问题
     * 设置成功则返回null，失败表示已经存在锁，返回正在操作的任务的操作时间
     *
     * @param context
     * @return
     */
    private String setCacheLock(SendDetailMessage context) {
        String redisKey = this.getRedisCacheKey(context);
        try {
            // 避免消费数据重复逻辑 插入redis 如果插入失败 说明有其他线程正在消费相同数据信息
            if (!redisClientCache.set(redisKey, context.getOperateTime().toString(), REDIS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS, false)) {
                return redisClientCache.get(redisKey);
            }
        } catch (Exception e) {
            logger.error("[dmsWorkSendDetail消费]设置Redis并发锁时发生异常，redisKey:" + redisKey, e);
        }
        return null;
    }

    /**
     * 删除Redis锁
     *
     * @param context
     */
    private void delCacheLock(SendDetailMessage context) {
        String redisKey = this.getRedisCacheKey(context);
        try {
            redisClientCache.del(redisKey);
        } catch (Exception e) {
            logger.error("[dmsWorkSendDetail消费]删除Redis并发锁时发生异常，redisKey:" + redisKey, e);
        }
    }

    private String getRedisCacheKey(SendDetailMessage context) {
        return REDIS_CACHE_KEY + context.getPackageBarcode() + SEPARATOR + context.getCreateSiteCode();
    }

    /**
     * 消费逻辑
     *
     * @param sendDetail
     */
    private void doConsume(SendDetailMessage sendDetail) {
        String packageBarCode = sendDetail.getPackageBarcode();
        if (SerialRuleUtil.isWaybillOrPackageNo(packageBarCode)) {
            String waybillCode = WaybillUtil.getWaybillCode(packageBarCode);
            BaseEntity<BigWaybillDto> baseEntity = this.getWaybillBaseEntity(waybillCode);
            if (baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                Waybill waybill = baseEntity.getData().getWaybill();
                if (BusinessUtil.isRMA(waybill.getWaybillSign())) {
                    if (!rmaHandOverWaybillService.buildAndStorage(sendDetail, waybill, baseEntity.getData().getGoodsList())) {
                        throw new RuntimeException("[dmsWorkSendDetail消费]存储RMA订单数据失败，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
                    }
                }
                this.sendInspection(sendDetail, waybillCode ,waybill.getWaybillType());
            } else {
                logger.warn("[dmsWorkSendDetail消费]根据运单号获取运单信息为空，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
            }
        } else {
            logger.warn("[dmsWorkSendDetail消费]无效的运单号/包裹号，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
        }
    }

    /**
     * 调用运单JSF接口获取运单基础数据信息
     *
     * @param waybillCode
     * @return
     */
    private BaseEntity<BigWaybillDto> getWaybillBaseEntity(String waybillCode) {
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryWaybillM(false);
        choice.setQueryGoodList(true);
        return waybillQueryManager.getDataByChoice(waybillCode, choice);
    }

    /**
     * 如果没有正逆向验货记录补发验货回传
     */
    private void sendInspection(SendDetailMessage sendDetail, String waybillCode, Integer waybillType) {
        // 如果是非LBP类型的订单直接返回
        if (waybillType != null && Constants.POP_LBP.equals(waybillType)) {
            if (!checkInspection(waybillCode, sendDetail.getPackageBarcode())) {
                // TODO: 2019/3/1 时间暂时取操作时间，原逻辑取create_time，需要沟通确认
                sendMQToPop(waybillCode, DateHelper.toDate(sendDetail.getOperateTime()));
            }
        }
    }

    /**
     * 自营
     */
    public static final Integer BUSINESS_TYPE_ONE = 10;

    /**
     * 如果没有正逆向验货记录补发验货回传
     */
    private boolean checkInspection(String waybillCode, String packageBarCode) {
        boolean flag;
        Inspection inspection = new Inspection();
        if (WaybillUtil.isWaybillCode(waybillCode)) {
            inspection.setPackageBarcode(packageBarCode);
            inspection.setInspectionType(BUSINESS_TYPE_ONE);
            flag = inspectionService.haveInspection(inspection);
        } else {
            flag = true;
        }
        return flag;
    }

    private void sendMQToPop(String waybillCode, Date createTime) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        sb.append("<OrderSendDetail xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        sb.append("<id>").append(waybillCode).append("</id>");
        String date = DateHelper.formatDate(createTime, "yyyy-MM-dd HH:mm:ss");
        sb.append("<receiveTime>").append(date).append("</receiveTime>");
        sb.append("</OrderSendDetail>");
        logger.info("snedMQpop----snedMQpop----" + sb.toString());
        pop1MQ.sendOnFailPersistent(waybillCode, sb.toString());
    }

    private void dmsToVendorMQ(SendDetailMessage sendDetail, Waybill waybill){
        // 发货目的地是车队，且是非城配运单，要通知调度系统
        if (waybill != null && Constants.BASE_SITE_MOTORCADE.equals(rbDto.getSiteType()) && !BusinessHelper.isDmsToVendor(waybill.getWaybillSign(), waybill.getSendPay())) {
            Message sendDispatchMessage = parseSendDetailToMessageOfDispatch(sendDetail, waybill, rbDto.getSiteName(), dmsToVendor.getTopic(), Constants.SEND_DETAIL_SOUCRE_NORMAL);
            this.logger.info("非城配运单，发车队通知调度系统发送MQ[" + sendDispatchMessage.getTopic() + "],业务ID[" + sendDispatchMessage.getBusinessId() + "],消息主题: " + sendDispatchMessage.getText());
            dmsToVendor.sendOnFailPersistent(sendDispatchMessage.getBusinessId(), sendDispatchMessage.getText());
        }
    }

    /**
     * 构建非城配运单发往车队通知调度系统MQ消息体
     * @param sendDetail
     * @param waybill
     * @param receiveSiteName
     * @param topic
     * @param source
     * @return
     */
    private Message parseSendDetailToMessageOfDispatch(SendDetailMessage sendDetail, Waybill waybill, String receiveSiteName, String topic, String source) {
        Message message = new Message();
        SendDispatchDto dto = new SendDispatchDto();
        if (sendDetail != null) {
            // MQ包含的信息:包裹号,发货站点,发货时间,组板发货时包含板号
            dto.setPackageBarcode(sendDetail.getPackageBarcode());
            dto.setWaybillCode(waybill.getWaybillCode());
            dto.setCreateSiteCode(sendDetail.getCreateSiteCode());
            dto.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
            dto.setReceiveSiteName(receiveSiteName);
            dto.setWaybillSign(waybill.getWaybillSign());
            dto.setEndProvinceId(waybill.getProvinceId());
            dto.setEndCityId(waybill.getCityId());
            dto.setEndAddress(waybill.getReceiverAddress());
            dto.setReceiverName(waybill.getReceiverName());
            dto.setReceiverPhone(waybill.getReceiverMobile());
            dto.setPaymentType(waybill.getPayment());
            dto.setBusiId(waybill.getBusiId());
            dto.setOrderTime(waybill.getOrderSubmitTime());
            dto.setOperateTime(DateHelper.toDate(sendDetail.getOperateTime()));
            dto.setSendCode(sendDetail.getSendCode());
            dto.setCreateUserCode(sendDetail.getCreateUserCode());
            dto.setCreateUser(sendDetail.getCreateUser());
            dto.setSource(source);
            dto.setBoxCode(sendDetail.getBoxCode());
            // TODO: 2019/3/1 需要在sendDetail消息体中添加板号信息
            dto.setBoardCode(sendDetail.getBoardCode());
            message.setTopic(topic);
            message.setText(JSON.toJSONString(dto));
            message.setBusinessId(sendDetail.getPackageBarcode());
        }
        return message;
    }

}
