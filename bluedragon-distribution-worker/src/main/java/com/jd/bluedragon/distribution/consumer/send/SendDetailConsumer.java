package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.distribution.send.domain.SendDispatchDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.alibaba.fastjson.JSON;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("dmsToVendor")
    private DefaultJMQProducer dmsToVendor;

    //added by hanjiaxing 2016.12.20
    @Autowired
    private GantryExceptionService gantryExceptionService;

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
                this.dmsToVendorMQ(sendDetail, waybill);
                this.updateGantryExceptionStatus(sendDetail);
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

    private void dmsToVendorMQ(SendDetailMessage sendDetail, Waybill waybill) {
        BaseStaffSiteOrgDto receiveSiteDto = this.getBaseStaffSiteDto(sendDetail.getReceiveSiteCode());
        // 发货目的地是车队，且是非城配运单，要通知调度系统
        if (waybill != null && Constants.BASE_SITE_MOTORCADE.equals(receiveSiteDto.getSiteType()) && !BusinessHelper.isDmsToVendor(waybill.getWaybillSign(), waybill.getSendPay())) {
            Message message = parseSendDetailToMessageOfDispatch(sendDetail, waybill, receiveSiteDto, dmsToVendor.getTopic(), Constants.SEND_DETAIL_SOUCRE_NORMAL);
            this.logger.info("非城配运单，发车队通知调度系统发送MQ[" + message.getTopic() + "],业务ID[" + message.getBusinessId() + "],消息主题: " + message.getText());
            dmsToVendor.sendOnFailPersistent(message.getBusinessId(), message.getText());
        }
    }

    private BaseStaffSiteOrgDto getBaseStaffSiteDto(Integer siteCode) {
        BaseStaffSiteOrgDto baseSiteDto = null;
        try {
            baseSiteDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        } catch (Exception e) {
            this.logger.error("发货全程跟踪调用站点信息异常", e);
        }

        if (baseSiteDto == null) {
            baseSiteDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(siteCode));
        }
        return baseSiteDto;
    }

    /**
     * 构建非城配运单发往车队通知调度系统MQ消息体
     * @param sendDetail
     * @param waybill
     * @param rbDto
     * @param topic
     * @param source
     * @return
     */
    private Message parseSendDetailToMessageOfDispatch(SendDetailMessage sendDetail, Waybill waybill, BaseStaffSiteOrgDto rbDto, String topic, String source) {
        Message message = new Message();
        SendDispatchDto dto = new SendDispatchDto();
        if (sendDetail != null) {
            // MQ包含的信息:包裹号,发货站点,发货时间,组板发货时包含板号
            dto.setPackageBarcode(sendDetail.getPackageBarcode());
            dto.setWaybillCode(waybill.getWaybillCode());
            dto.setCreateSiteCode(sendDetail.getCreateSiteCode());
            dto.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
            dto.setReceiveSiteName(rbDto.getSiteName());
            dto.setOperateTime(new Date(sendDetail.getOperateTime()));
            dto.setSendCode(sendDetail.getSendCode());
            dto.setWaybillSign(waybill.getWaybillSign());
            dto.setSource(source);
            dto.setCreateUserCode(sendDetail.getCreateUserCode());
            dto.setCreateUser(sendDetail.getCreateUser());
            dto.setBoxCode(sendDetail.getBoxCode());
            dto.setBoardCode(sendDetail.getBoardCode());

            dto.setEndProvinceId(waybill.getProvinceId());
            dto.setEndCityId(waybill.getCityId());
            dto.setEndAddress(waybill.getReceiverAddress());
            dto.setReceiverName(waybill.getReceiverName());
            dto.setReceiverPhone(waybill.getReceiverMobile());
            dto.setPaymentType(waybill.getPayment());
            dto.setBusiId(waybill.getBusiId());
            dto.setOrderTime(waybill.getOrderSubmitTime());

            dto.setSendPay(waybill.getSendPay());
            dto.setReceiveDmsSiteCode(rbDto.getDmsSiteCode());
            dto.setPreSiteCode(waybill.getOldSiteId());

            try {
                BaseStaffSiteOrgDto preSiteDto = this.baseMajorManager.getBaseSiteBySiteId(waybill.getOldSiteId());
                if(preSiteDto != null){
                    dto.setPreSiteName(preSiteDto.getSiteName());
                }
            } catch (Exception e) {
                logger.error("非城配运单，发车队通知调度系统构建MQ时查询预分拣站点信息异常，MQ不在发送预分拣站点名称：" + sendDetail.getPackageBarcode(), e);
            }
            message.setTopic(topic);
            message.setText(JSON.toJSONString(dto));
            message.setBusinessId(sendDetail.getPackageBarcode());
        }
        return message;
    }

    /**
     * 更新发货状态
     *
     * @param sendDetail
     */
    private void updateGantryExceptionStatus(SendDetailMessage sendDetail) {
        // TODO: 2019/3/1  此处逻辑需拆解出去 拆到消费sendDetail MQ里
        //added by hanjiaxing 2016.12.20 reason:update gantry_exception set send_status = 1
        gantryExceptionService.updateSendStatus(sendDetail.getBoxCode(), Long.valueOf(sendDetail.getCreateSiteCode()));
        this.logger.info("更新异常信息发货状态，箱号：" + sendDetail.getBoxCode());
    }

}
