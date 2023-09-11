package com.jd.bluedragon.distribution.consumer.send;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SmsMessageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.MessageException;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.dto.SiteCodeAssociationDto;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.dto.CCInAndOutBoundMessage;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainOperateTypeEnum;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainSendService;
import com.jd.bluedragon.distribution.fastRefund.domain.FastRefundBlockerComplete;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.service.DmsSendRelationService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.ColdChainSendMessage;
import com.jd.bluedragon.distribution.send.domain.DmsSendToEmsWaybillInfoMq;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.distribution.send.domain.SendDispatchDto;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sms.domain.SMSDto;
import com.jd.bluedragon.distribution.sms.service.SmsConfigService;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.common.util.StringUtils;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillPickup;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 发货明细MQ[dmsWorkSendDetail] 消费逻辑
 *
 * @author lixin39
 */
@Service("sendDetailConsumer")
public class SendDetailConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(SendDetailConsumer.class);

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
    @Qualifier("dmsColdChainSendWaybill")
    private DefaultJMQProducer dmsColdChainSendWaybill;

    @Autowired
    private ColdChainSendService coldChainSendService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Autowired
    private SmsMessageManager smsMessageManager;

    @Autowired
    private SmsConfigService smsConfigService;

    @Autowired
    private LogEngine logEngine;

    @Autowired
    @Qualifier("ccInAndOutBoundProducer")
    private DefaultJMQProducer ccInAndOutBoundProducer;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Autowired
    @Qualifier("kyStorageSendMQProducer")
    private DefaultJMQProducer kyStorageSendMQProducer;

    @Autowired
    @Qualifier("dmsSendRelationService")
    private DmsSendRelationService dmsSendRelationService;

    @Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;

    @Qualifier("blockerComOrbrefundRqMQ")
    @Autowired
    private DefaultJMQProducer blockerComOrbrefundRqMQ;
    
    @Autowired
    @Qualifier("dmsSendToEmsWaybillInfoProducer")
    private DefaultJMQProducer dmsSendToEmsWaybillInfoProducer;

    /**
     * 缓存redis的key
     */
    private final static String REDIS_CACHE_KEY = "MQ-CONSUMER-SEND-DETAIL-";

    /**
     * 缓存redis的到期时间，5s
     */
    private final static long REDIS_CACHE_EXPIRE_TIME = 5;

    /**
     * 冷链卡班缓存redis的key
     * */
    private final static String REDIS_COLD_CHAIN_STORAGE_SMS = "COLD_CHAIN_STORAGE_SMS-";
    /**
     * 冷链卡班缓存redis的过期时间，24h
     * */
    @Value("${sendDetailConsumer.smsExpireTime:24}")
    private Integer smsExpireTime;

    /**
     * 分隔符号
     */
    private final static String SEPARATOR = "-";

    @Value("${pack.send.cache.time:30}")
    private Integer packSendCacheTime;

    /**
     * 冷链产品需要拦截快退缓存redis的key
     * */
    private final static String REDIS_COLD_INTEGRCEPT_SMS = "COLD_CHAIN_INTEGRCEPT_SMS-";

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private TaskService taskService;

    @Override
    public void consume(Message message) {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[dmsWorkSendDetail消费]MQ-消息体非JSON格式，内容为【{}】", message.getText());
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
                        throw new MessageException("[dmsWorkSendDetail消费]该任务已被锁定，抛出异常进行重试，MQ message body:" + message.getText());
                    } else {
                        // 无效
                        log.warn("[dmsWorkSendDetail消费]无效发货明细消息，重复操作，MQ message body:{}", message.getText());
                        return;
                    }
                }
            } else {
                log.warn("[dmsWorkSendDetail消费]无效发货明细消息，包裹号或者操作时间错误，MQ message body:{}", message.getText());
            }
        } catch (MessageException e) {
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + message.getText(), e);
        } catch (Exception e) {
            log.error("[dmsWorkSendDetail消费]消费异常，MQ message body:{}", message.getText(), e);
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
            log.error("[dmsWorkSendDetail消费]设置Redis并发锁时发生异常，redisKey:{}", redisKey, e);
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
            log.error("[dmsWorkSendDetail消费]删除Redis并发锁时发生异常，redisKey:{}", redisKey, e);
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
    private void doConsume(SendDetailMessage sendDetail) throws JMQException {
        String packageBarCode = sendDetail.getPackageBarcode();
        if (SerialRuleUtil.isWaybillOrPackageNo(packageBarCode)) {
            String waybillCode = WaybillUtil.getWaybillCode(packageBarCode);
            BaseEntity<BigWaybillDto> baseEntity = this.getWaybillBaseEntity(waybillCode);
            if (baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                Waybill waybill = baseEntity.getData().getWaybill();
                WaybillPickup waybillPickup = baseEntity.getData().getWaybillPickup();
                if (BusinessUtil.isRMA(waybill.getWaybillSign())) {
                    if (!rmaHandOverWaybillService.buildAndStorage(sendDetail, waybill, baseEntity.getData().getGoodsList())) {
                        throw new RuntimeException("[dmsWorkSendDetail消费]存储RMA订单数据失败，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
                    }
                }
                // 杭州亚运会特殊全程跟踪处理
                this.handleSecurityCheckWaybillTrace(sendDetail);
                // 非城配运单，发车队通知调度系统发送MQ消息
                this.dmsToVendorMQ(sendDetail, waybill);
                // 构建并发送冷链发货MQ消息 - 运输计划相关
                this.sendColdChainSendMQ(sendDetail, waybillCode);
                // 龙门架、分拣机发货更新发货异常状态
                this.updateGantryExceptionStatus(sendDetail);
                // 冷链暂存收费发短信
                this.coldChainStorageSMS(sendDetail,waybill);
                // 推送冷链操作MQ消息 - B2B冷链卸货出入库业务相关
                this.pushColdChainOperateMQ(sendDetail, waybill.getWaybillSign());
                // 快运暂存发货MQ
                this.kyStorageSendMq(sendDetail);
                // 保存发货关系
                this.saveSendRelation(sendDetail);
                //处理冷链拦截快退
                this.doColdIntercept(waybill,sendDetail);
                // 构建并发送MQ给邮管局消息
                this.sendToEmsWaybillInfoMQ(sendDetail, waybill,waybillPickup);
            } else {
                log.warn("[dmsWorkSendDetail消费]根据运单号获取运单信息为空，packageBarCode:{},boxCode:{}", packageBarCode, sendDetail.getBoxCode());
            }
        } else {
            log.warn("[dmsWorkSendDetail消费]无效的运单号/包裹号，packageBarCode:{},boxCode:{}", packageBarCode, sendDetail.getBoxCode());
        }
    }
    /**
     * 判断是否发邮政单子，发出消息
     *
     * @param sendDetail
     * @param waybill
     */
    private void sendToEmsWaybillInfoMQ(SendDetailMessage sendDetail, Waybill waybill, WaybillPickup waybillPickup) {
    	Integer preSiteCode = waybill.getOldSiteId();
    	Integer receiveSiteCode = sendDetail.getReceiveSiteCode();
    	//判断发货站点是预分拣站点
        if(!ObjectUtils.equals(preSiteCode, receiveSiteCode)) {
        	return;
        }
        BaseStaffSiteOrgDto receiveSiteDto = this.getBaseStaffSiteDto(sendDetail.getReceiveSiteCode());
    	//判断发货站点是否三方邮政落地配-16001
        boolean isThirdSmsSite = false;
        if(receiveSiteDto != null 
        		&& Constants.THIRD_SITE_TYPE.equals(receiveSiteDto.getSiteType())
        		&& Constants.THIRD_SITE_SUB_TYPE.equals(receiveSiteDto.getSubType())
        		&& Constants.THIRD_SITE_THIRD_TYPE_SMS.equals(receiveSiteDto.getThirdType())) {
        	isThirdSmsSite = true;
        }
        if(!isThirdSmsSite) {
        	return;
        }
        // 发货目的地是邮政站点，发出mq
        Message message = parseSendDetailToSendEmsMq(sendDetail, waybill,waybillPickup, receiveSiteDto,receiveSiteDto, dmsSendToEmsWaybillInfoProducer.getTopic(), Constants.SEND_DETAIL_SOUCRE_NORMAL);
        this.log.info("分拣发邮政站点，发送MQ[{}],业务ID[{}],消息主题: {}", message.getTopic(), message.getBusinessId(), message.getText());
        dmsSendToEmsWaybillInfoProducer.sendOnFailPersistent(message.getBusinessId(), message.getText());
    }
    /**
     * 构建发邮政MQ消息体
     *
     * @param sendDetail
     * @param waybillPickup
     * @param waybill
     * @param rbDto
     * @param topic
     * @param source
     * @return
     */
    private Message parseSendDetailToSendEmsMq(SendDetailMessage sendDetail, Waybill waybill, WaybillPickup waybillPickup, BaseStaffSiteOrgDto rbDto,BaseStaffSiteOrgDto preSiteDto, String topic, String source) {
        Message message = new Message();
        DmsSendToEmsWaybillInfoMq dto = new DmsSendToEmsWaybillInfoMq();
        if (sendDetail != null) {
            // MQ包含的信息:包裹号,发货站点,发货时间,组板发货时包含板号
            dto.setPackageBarcode(sendDetail.getPackageBarcode());
            dto.setWaybillCode(waybill.getWaybillCode());
            dto.setCreateSiteCode(sendDetail.getCreateSiteCode());
            dto.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
            dto.setReceiveSiteName(rbDto.getSiteName());
            dto.setOperateTime(new Date(sendDetail.getOperateTime()));
            dto.setOperatorId(sendDetail.getCreateUserCode());
            dto.setOperatorName(sendDetail.getCreateUser());
            dto.setSendCode(sendDetail.getSendCode());
            dto.setWaybillSign(waybill.getWaybillSign());
            dto.setSource(source);
            dto.setCreateUserCode(sendDetail.getCreateUserCode());
            dto.setCreateUser(sendDetail.getCreateUser());
            dto.setBoxCode(sendDetail.getBoxCode());
            dto.setBoardCode(sendDetail.getBoardCode());

            dto.setReceiverName(waybill.getReceiverName());
            dto.setPaymentType(waybill.getPayment());
            dto.setBusiId(waybill.getBusiId());
            dto.setOrderTime(waybill.getOrderSubmitTime());

            dto.setSendPay(waybill.getSendPay());
            dto.setReceiveDmsSiteCode(rbDto.getDmsSiteCode());
            dto.setPreSiteCode(waybill.getOldSiteId());
            dto.setPreSiteName(preSiteDto.getSiteName());
            
            dto.setConsigner(waybill.getConsigner());
        	dto.setConsignerMobile(waybill.getConsignerMobile());
        	dto.setConsignerAddress(waybill.getConsignerAddress());
        	if(waybillPickup != null) {
                dto.setConsignerProvinceId(waybillPickup.getConsignerProvinceId());
                dto.setConsignerCityId(waybillPickup.getConsignerCityId());
                dto.setConsignerCountryId(waybillPickup.getConsignerCountyId());
                dto.setConsignerTownId(waybillPickup.getConsignerTownId());
        	}
        	
        	dto.setReceiverName(waybill.getReceiverName());
        	dto.setReceiverMobile(waybill.getReceiverMobile());
        	dto.setReceiverAddress(waybill.getReceiverAddress());
        	
            dto.setReceiverProvinceId(waybill.getProvinceId());
            dto.setReceiverCityId(waybill.getCityId());
            dto.setReceiverCountryId(waybill.getCountryId());
            dto.setReceiverTownId(waybill.getTownId());
        	
            message.setTopic(topic);
            message.setText(JSON.toJSONString(dto));
            message.setBusinessId(sendDetail.getPackageBarcode());
            
        }
        return message;
    }    
    private void saveSendRelation(SendDetailMessage sendDetail) {
		DmsSendRelation dmsSendRelation = new DmsSendRelation();
		dmsSendRelation.setOriginalSiteCode(sendDetail.getCreateSiteCode());
		dmsSendRelation.setDestinationSiteCode(sendDetail.getReceiveSiteCode());
		if(!dmsSendRelationService.saveWithFrequency(dmsSendRelation)){
			log.warn("流向关系并保存失败！消息体：" + JsonHelper.toJson(dmsSendRelation));
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
        choice.setQueryWaybillExtend(true);
        choice.setQueryWaybillM(false);
        choice.setQueryGoodList(true);
        choice.setQueryWaybillP(true);
        return waybillQueryManager.getDataByChoice(waybillCode, choice);
    }

    /**
     * 非城配运单，发车队通知调度系统发送MQ
     *
     * @param sendDetail
     * @param waybill
     */
    private void dmsToVendorMQ(SendDetailMessage sendDetail, Waybill waybill) {
        BaseStaffSiteOrgDto receiveSiteDto = this.getBaseStaffSiteDto(sendDetail.getReceiveSiteCode());
        // 发货目的地是车队，且是非城配运单，要通知调度系统
        if (waybill != null && Constants.BASE_SITE_MOTORCADE.equals(receiveSiteDto.getSiteType()) && !BusinessHelper.isDmsToVendor(waybill.getWaybillSign(), waybill.getSendPay())) {
            Message message = parseSendDetailToMessageOfDispatch(sendDetail, waybill, receiveSiteDto, dmsToVendor.getTopic(), Constants.SEND_DETAIL_SOUCRE_NORMAL);
            this.log.debug("非城配运单，发车队通知调度系统发送MQ[{}],业务ID[{}],消息主题: {}", message.getTopic(), message.getBusinessId(), message.getText());
            dmsToVendor.sendOnFailPersistent(message.getBusinessId(), message.getText());
        }
    }

    /**
     * 构建并发送冷链发货MQ消息
     *
     * @param sendDetail
     * @param waybillCode
     * @throws JMQException
     */
    private void sendColdChainSendMQ(SendDetailMessage sendDetail, String waybillCode) throws JMQException {
        if (SendBizSourceEnum.getEnum(sendDetail.getBizSource()) == SendBizSourceEnum.COLD_CHAIN_SEND
                || SendBizSourceEnum.getEnum(sendDetail.getBizSource()) == SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND
                || SendBizSourceEnum.getEnum(sendDetail.getBizSource()) == SendBizSourceEnum.COLD_LOAD_CAR_SEND
                || SendBizSourceEnum.getEnum(sendDetail.getBizSource()) == SendBizSourceEnum.COLD_LOAD_CAR_SEND_NEW
                || SendBizSourceEnum.getEnum(sendDetail.getBizSource()) == SendBizSourceEnum.COLD_LOAD_CAR_KY_SEND_NEW
        ) {
            BaseStaffSiteOrgDto createSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
            BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());
            if (createSiteDto != null && receiveSiteDto != null) {
                ColdChainSend coldChainSend = coldChainSendService.getBySendCode(waybillCode, sendDetail.getSendCode());
                if (coldChainSend != null && StringUtils.isNotEmpty(coldChainSend.getTransPlanCode())) {
                    ColdChainSendMessage message = new ColdChainSendMessage();
                    message.setWaybillCode(waybillCode);
                    message.setSendCode(sendDetail.getSendCode());
                    // 发货
                    message.setSendType(1);
                    message.setTransPlanCode(coldChainSend.getTransPlanCode());
                    message.setCreateSiteCode(createSiteDto.getDmsSiteCode());
                    message.setReceiveSiteCode(receiveSiteDto.getDmsSiteCode());
                    message.setOperateTime(sendDetail.getOperateTime());
                    message.setOperateUserName(sendDetail.getCreateUser());
                    if (sendDetail.getCreateUserCode() != null) {
                        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(sendDetail.getCreateUserCode());
                        if (dto != null) {
                            message.setOperateUserErp(dto.getErp());
                        }
                    }
                    dmsColdChainSendWaybill.send(waybillCode, JSON.toJSONString(message));
                }
            }
        }
    }

    private BaseStaffSiteOrgDto getBaseStaffSiteDto(Integer siteCode) {
        BaseStaffSiteOrgDto baseSiteDto = null;
        try {
            baseSiteDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        } catch (Exception e) {
            this.log.error("发货全程跟踪调用站点信息异常,siteCode={}", siteCode, e);
        }

        if (baseSiteDto == null) {
            baseSiteDto = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(siteCode));
        }
        return baseSiteDto;
    }

    /**
     * 构建非城配运单发往车队通知调度系统MQ消息体
     *
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
                if (preSiteDto != null) {
                    dto.setPreSiteName(preSiteDto.getSiteName());
                }
            } catch (Exception e) {
                log.error("非城配运单，发车队通知调度系统构建MQ时查询预分拣站点信息异常，MQ不在发送预分拣站点名称：{}", sendDetail.getPackageBarcode(), e);
            }
            message.setTopic(topic);
            message.setText(JSON.toJSONString(dto));
            message.setBusinessId(sendDetail.getPackageBarcode());
        }
        return message;
    }

    /**
     * 龙门架、分拣机发货更新发货异常状态
     *
     * @param sendDetail
     */
    private void updateGantryExceptionStatus(SendDetailMessage sendDetail) {
        //added by hanjiaxing 2016.12.20 reason:update gantry_exception set send_status = 1
        SendBizSourceEnum bizSource = SendBizSourceEnum.getEnum(sendDetail.getBizSource());
        if (bizSource == null || bizSource == SendBizSourceEnum.SCANNER_FRAME_SEND || bizSource == SendBizSourceEnum.SORT_MACHINE_SEND) {
            gantryExceptionService.updateSendStatus(sendDetail.getBoxCode(), Long.valueOf(sendDetail.getCreateSiteCode()));
            this.log.debug("更新异常信息发货状态，箱号：{}", sendDetail.getBoxCode());
        }
    }

    /**
     * 冷链暂存收费发短信
     *  1、冷链卡班纯配、月结自提或冷链卡班仓配、月结自提
     *  2、目的分拣中心操作发货
     * @param sendDetail
     * @param waybill
     */
    private void coldChainStorageSMS(SendDetailMessage sendDetail,Waybill waybill) {
        long startTime = System.currentTimeMillis();
        try {
            String waybillSign = waybill.getWaybillSign();
            if(!(BusinessUtil.isMonthSelf(waybillSign)
                    && (BusinessUtil.isColdKBPureMatch(waybillSign) || BusinessUtil.isColdKBWmsSend(waybillSign)))){
                //非冷链卡班
                return;
            }
            BaseStaffSiteOrgDto oldSiteDto = baseMajorManager.getBaseSiteBySiteId(waybill.getOldSiteId());
            if(oldSiteDto != null && oldSiteDto.getDmsId() != null){
                Integer operateSiteCode = sendDetail.getCreateSiteCode();
                if(!operateSiteCode.equals(oldSiteDto.getDmsId())){
                    //不是目的分拣中心
                    return;
                }
                String waybillCode = WaybillUtil.getWaybillCode(sendDetail.getPackageBarcode());
                if(StringUtils.isBlank(waybillCode)){
                    return;
                }
                try {
                    String redisKey = REDIS_COLD_CHAIN_STORAGE_SMS + waybillCode;
                    boolean isExist = redisClientCache.set(redisKey, redisKey, smsExpireTime,
                            TimeUnit.HOURS, false);
                    if(!isExist){
                        //默认设置10小时的去重
                        return;
                    }
                }catch (Exception e){
                    log.error("获取{}冷链卡班发短信缓存失败！", waybillCode, e);
                }

                Integer orgId = oldSiteDto.getOrgId();
                SMSDto sMSDto = smsConfigService.getSMSConstantsByOrgId(orgId);
                if(sMSDto == null){
                    log.warn("目的分拣中心不属于7大区,目的分拣中心：{}所属区域：{}",operateSiteCode,orgId);
                    return;
                }
                String senderNum = sMSDto.getAccount();
                Long templateId = sMSDto.getTemplateId();
                String token = sMSDto.getToken();

                BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(operateSiteCode);
                if(operateSite == null){
                    return;
                }
                String[] templateParam = new String[]{waybillCode,operateSite.getSiteName(),operateSite.getSitePhone(),operateSite.getAddress()};
                String mobileNum = waybill.getReceiverMobile();
                String extension = Constants.DMS_COLD_CHAIN_SEND;
                InvokeResult result = smsMessageManager.sendSmsTemplateMessage(senderNum,templateId,
                        templateParam,mobileNum,token,extension);
                if(result.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    log.error("冷链暂存收费发短信失败,失败原因：{}",result.getMessage());
                }
                try {
                    //记录日志系统
                    long endTime = System.currentTimeMillis();
                    JSONObject request = new JSONObject();
                    request.put("waybillCode", waybillCode);
                    request.put("packageCode", sendDetail.getPackageBarcode());
                    request.put("sendCode", sendDetail.getSendCode());
                    request.put("siteCode", oldSiteDto.getDmsId());
                    request.put("siteName", oldSiteDto.getDmsName());
                    request.put("operatorCode", sendDetail.getCreateUserCode());
                    request.put("operatorName", sendDetail.getCreateUser());

                    JSONObject response = new JSONObject();
                    response.put("resultCode", result.getCode());
                    response.put("resultMessage", result.getMessage());
                    response.put("senderNum", senderNum);
                    response.put("templateId", templateId);
                    response.put("templateParam", JsonHelper.toJson(templateParam));
                    response.put("mobileNum", mobileNum);
                    response.put("token", token);
                    response.put("extension", extension);

                    BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                            .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_COLDCHAIN_SMS)
                            .processTime(endTime,startTime)
                            .operateRequest(request)
                            .operateResponse(response)
                            .methodName("SendDetailConsumer#coldChainStorageSMS")
                            .build();
                    logEngine.addLog(businessLogProfiler);
                }catch (Exception e){
                    log.error("冷链卡班暂存计费发短信记录日志异常！",e);
                }

            }
        }catch (Exception e){
            log.error("冷链暂存收费发短信异常!",e);
        }

    }

    /**
     * 推送冷链操作MQ消息 - B2B冷链卸货出入库业务相关
     * 冷链消费
     *
     * @param sendDetail
     * @param waybillSign
     * @throws JMQException
     */
    private void pushColdChainOperateMQ(SendDetailMessage sendDetail, String waybillSign) throws JMQException {
        if (!(BusinessUtil.isColdChainKBWaybill(waybillSign) || BusinessUtil.isColdChainCityDeliveryWaybill(waybillSign))) {
            return ;
        }

        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());

        CCInAndOutBoundMessage body = new CCInAndOutBoundMessage();
        body.setOrgId(String.valueOf(siteOrgDto.getOrgId()));
        body.setOrgName(siteOrgDto.getOrgName());
        body.setProvinceAgencyCode(siteOrgDto.getProvinceAgencyCode());
        body.setProvinceAgencyName(siteOrgDto.getProvinceAgencyName());
        body.setAreaHubCode(siteOrgDto.getAreaCode());
        body.setAreaHubName(siteOrgDto.getAreaName());
        body.setSiteId(String.valueOf(siteOrgDto.getSiteCode()));
        body.setSiteName(siteOrgDto.getSiteName());
        body.setOperateType(ColdChainOperateTypeEnum.DELIVERY.getType());
        Integer userCode = sendDetail.getCreateUserCode();
        if (userCode != null) {
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(userCode);
            if (dto != null) {
                body.setOperateERP(dto.getErp());
            }
        }

        body.setOperateTime(DateHelper.formatDateTime(new Date(sendDetail.getOperateTime())));
        body.setPackageNo(sendDetail.getPackageBarcode());
        body.setWaybillNo(WaybillUtil.getWaybillCode(sendDetail.getPackageBarcode()));
        ccInAndOutBoundProducer.send(sendDetail.getPackageBarcode(), JSON.toJSONString(body));
    }

    /**
     * 快运暂存发货MQ
     * @param sendDetail
     */
    private void kyStorageSendMq(SendDetailMessage sendDetail) {
        try {
            String waybillCode = WaybillUtil.getWaybillCode(sendDetail.getPackageBarcode());
            // 非暂存站点发货直接返回
            StoragePackageM storagePackageM = storagePackageMService.getStoragePackageM(waybillCode);
            if(storagePackageM == null || storagePackageM.getCreateSiteCode() == null
                    || sendDetail.getCreateSiteCode() == null
                    || storagePackageM.getCreateSiteCode().intValue() != sendDetail.getCreateSiteCode()){
                return;
            }
            if(log.isInfoEnabled()){
                log.info("快运暂存发货发送MQ【{}】,业务ID【{}】",kyStorageSendMQProducer.getTopic(),sendDetail.getPackageBarcode());
            }
            kyStorageSendMQProducer.sendOnFailPersistent(sendDetail.getPackageBarcode(),JsonHelper.toJson(sendDetail));
        }catch (Exception e){
            log.error("快运暂存发货MQ异常，单号：【{}】",sendDetail.getPackageBarcode(),e);
        }
    }
    /**
     * 处理冷链拦截快退
     * @param waybill
     * @param sendDetail
     */
    private void doColdIntercept(Waybill waybill,SendDetailMessage sendDetail){

        if(waybill == null || waybill.getWaybillExt() == null || StringUtils.isBlank(waybill.getWaybillExt().getProductType()) || sendDetail == null){
            log.warn("处理冷链拦截快退逻辑，运单详情或发货明细为空");
            return;
        }
        String waybillCode = waybill.getWaybillCode();
        try {
            List<String> productTypes = Arrays.asList(waybill.getWaybillExt().getProductType().split(Constants.SEPARATOR_COMMA));
            //冷链卡班、医药大票、冷链小票、医药零担、医冷零担
            boolean isColdProductType = productTypes.contains(DmsConstants.PRODUCT_TYPE_COLD_CHAIN_KB)
                                        || productTypes.contains(Constants.PRODUCT_TYPE_MEDICINE_DP)
                                        || productTypes.contains(Constants.PRODUCT_TYPE_COLD_CHAIN_XP)
                                        || productTypes.contains(Constants.PRODUCT_TYPE_MEDICINE_COLD)
                                        || productTypes.contains(Constants.PRODUCT_TYPE_MEDICAL_PART_BILL);
            if(!isColdProductType){
                log.warn("处理冷链拦截快退逻辑，非冷链产品类型，无须处理，运单号：{}",waybillCode);
                return;
            }
            Integer createSiteCode = sendDetail.getCreateSiteCode();
            //前缀 + 场地 + 运单号
            String redisKey = REDIS_COLD_INTEGRCEPT_SMS + createSiteCode + SEPARATOR + waybillCode;
            boolean isExist = redisClientCache.exists(redisKey);
            if(isExist){
                //设置30分钟的去重
                return;
            }
            redisClientCache.setEx(redisKey, Constants.YN_YES.toString(), this.packSendCacheTime, TimeUnit.MINUTES);
            //快退
            notifyBlocker(waybill,sendDetail);
            backwardSendMQ(waybill,sendDetail);
        }catch (Exception e){
            log.error("处理冷链拦截快退逻辑发成异常，运单号：" + waybillCode, e);
        }
    }

    /**
     * 发送bd_blocker_complete
     * @param waybill
     * @param sendDetail
     */
    private void notifyBlocker(Waybill waybill,SendDetailMessage sendDetail) {
        String wayBillCode = waybill.getWaybillCode();
        try {
            if (SendBizSourceEnum.REVERSE_SEND.getCode().equals(sendDetail.getBizSource())) {
                Date OperateTime = sendDetail.getOperateTime() == null? new Date():new Date(sendDetail.getOperateTime());
                String waybillsign = waybill.getWaybillSign();
                if (waybillsign != null && waybillsign.length() > 0) {
                    if (BusinessUtil.isSick(waybill.getWaybillSign())) {
                        this.log.warn("分拣中心逆向病单,冷链产品屏蔽退款100分MQ,运单号：{}", wayBillCode);
                        return;
                    }
                }
                String refundMessage = this.refundMessage(wayBillCode,
                        DateHelper.formatDateTimeMs(OperateTime));
                //bd_blocker_complete的MQ
                this.bdBlockerCompleteMQ.send(wayBillCode, refundMessage);
                this.log.info("冷链产品 退款100分MQ消息推送成功,运单号：{}", wayBillCode);
            }
        } catch (Exception e) {
            this.log.error("冷链产品回传退款100分逆向分拣信息失败，运单号：" + wayBillCode, e);
        }
    }
    private String refundMessage(String waybillCode, String operateTime) {
        StringBuilder message = new StringBuilder();
        message.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
        message.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        message.append("<OrderId>" + waybillCode + "</OrderId>");
        message.append("<OrderType>20</OrderType>");
        message.append("<MessageType>BLOCKER_QUEUE_DMS</MessageType>");
        message.append("<OperatTime>" + operateTime + "</OperatTime>");
        message.append("</OrderTaskInfo>");

        return message.toString();
    }

    /**
     * 发送blockerComOrbrefundRq
     * @param waybill
     * @param sendDetail
     */
    private void backwardSendMQ(Waybill waybill,SendDetailMessage sendDetail){
        String wayBillCode = waybill.getWaybillCode();
        // 验证运单号
        String waybillsign = waybill.getWaybillSign();
        if(waybillsign != null && waybillsign.length()>0){
            //waybillsign  1=T  ||  waybillsign  15=6表示逆向订单
            if((waybill.getWaybillSign().charAt(0)=='T' || waybill.getWaybillSign().charAt(14)=='6')){
                if(BusinessUtil.isSick(waybill.getWaybillSign())){
                    this.log.warn("分拣中心逆向病单,冷链产品屏蔽快退MQ,运单号：{}", wayBillCode);
                    return;
                }
                //组装FastRefundBlockerComplete
                FastRefundBlockerComplete frbc = toMakeFastRefundBlockerComplete(wayBillCode,sendDetail);
                String json = JsonHelper.toJson(frbc);
                this.log.info("冷链产品,分拣中心逆向订单快退:运单号[{}]",wayBillCode);
                try {
                    blockerComOrbrefundRqMQ.send(wayBillCode,json);
                    this.log.info("冷链产品发送blockerComOrbrefundRq成功,运单号：{}", wayBillCode);
                } catch (Exception e) {
                    this.log.error("冷链产品,分拣中心逆向订单快退MQ失败[{}]",json , e);
                }
            }else{
                log.info("订单:{}为非逆向订单,waybillsign:{}",wayBillCode,waybillsign);
            }
        }
    }

    /**
     * blockerComOrbrefundRq
     * 参数拼装
     * @param waybillCode
     * @param sendDetail
     * @return
     */
    private FastRefundBlockerComplete toMakeFastRefundBlockerComplete(String waybillCode,SendDetailMessage sendDetail){
        FastRefundBlockerComplete frbc = new FastRefundBlockerComplete();
        //新运单号获取老运单号的所有信息  参数返单号
        try{
            BaseEntity<Waybill> wayBillOld = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
            if(wayBillOld.getData() != null){
                String vendorId = wayBillOld.getData().getVendorId();
                if(vendorId == null || "".equals(vendorId)){
                    frbc.setOrderId("0");//没有订单号的外单,是非京东平台上下的订单
                }else{
                    frbc.setOrderId(vendorId);
                }
            }else{
                frbc.setOrderId("0");
            }
        }catch(Exception e){
            this.log.error("冷链产品,发送blockerComOrbrefundRq的MQ时新运单号获取老运单号失败,waybillcode:"+waybillCode, e);
        }
        Date operatorTime = sendDetail.getOperateTime() == null? new Date():new Date(sendDetail.getOperateTime());
        frbc.setWaybillcode(waybillCode);
        frbc.setApplyReason("分拣中心快速退款");
        frbc.setApplyDate(operatorTime.getTime());
        frbc.setSystemId(87);//blockerComOrbrefundRq的systemId设定为87
        frbc.setReqErp(String.valueOf(sendDetail.getCreateUserCode()));
        frbc.setReqName(sendDetail.getCreateUser());
        //写死是三方
        frbc.setOrderType(20);
        frbc.setMessageType("BLOCKER_QUEUE_DMS_REVERSE_PRINT");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        frbc.setOperatTime(dateFormat.format(operatorTime));
        frbc.setSys("ql.dms");

        return frbc;
    }

    /**
     * 处理案件全程跟踪
     * @param sendDetail
     * @author fanggang7
     * @time 2023-08-08 18:10:21 周一
     */
    private void handleSecurityCheckWaybillTrace(SendDetailMessage sendDetail) {
        try {
            // 读取系统配置
            final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_SECURITY_CHECK_SITE_ASSOCIATION + sendDetail.getCreateSiteCode());
            if (sysConfig == null) {
                return;
            }
            final SiteCodeAssociationDto siteCodeAssociationDto = JSON.parseObject(sysConfig.getConfigContent(), SiteCodeAssociationDto.class);
            if (siteCodeAssociationDto == null) {
                return;
            }
            // 判断是否是在配置范围中
            final List<Integer> siteCodeAssociationList = siteCodeAssociationDto.getSa();
            if(CollectionUtils.isEmpty(siteCodeAssociationList) || !siteCodeAssociationList.contains(sendDetail.getReceiveSiteCode())){
                return;
            }
            log.info("handleSecurityCheckWaybillTrace match {}", JsonHelper.toJson(sendDetail));
            // 发送全程跟踪消息
            this.sendWaybillTrace(sendDetail, WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK);
        } catch (Exception e) {
            log.error("handleSecurityCheckWaybillTrace exception {}", JsonHelper.toJson(sendDetail), e);
        }
    }

    private Long securityCheckAheadTimeMillseconds = 10000L;

    /**
     * 发送全程跟踪
     * @param operateType
     */
    private void sendWaybillTrace(SendDetailMessage sendDetail, Integer operateType) {
        try {
            WaybillStatus waybillStatus = new WaybillStatus();
            //设置站点相关属性
            waybillStatus.setPackageCode(sendDetail.getPackageBarcode());

            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
            waybillStatus.setCreateSiteCode(sendDetail.getCreateSiteCode());
            waybillStatus.setCreateSiteName(siteOrgDto != null ? siteOrgDto.getSiteName() : Constants.EMPTY_FILL);

            waybillStatus.setOperatorId(sendDetail.getCreateUserCode());
            waybillStatus.setOperator(sendDetail.getCreateUser());
            // 操作时间为发货时间的前10秒
            long operateTime = (sendDetail.getOperateTime() != null ? sendDetail.getOperateTime() : System.currentTimeMillis()) - securityCheckAheadTimeMillseconds;
            waybillStatus.setOperateTime(new Date(operateTime));

            waybillStatus.setOperateType(operateType);
            waybillStatus.setRemark(String.format("您的快件在【%s】已二次安检通过", waybillStatus.getCreateSiteName()));

            Map<String, Object> extendParamMap = new HashMap<>();
            extendParamMap.put("traceDisplay", 0);
            waybillStatus.setExtendParamMap(extendParamMap);

            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            log.error("发货发送安检全称跟踪失败 {}", JsonHelper.toJson(sendDetail), e);
        }
    }

    /**
     * 转换成全称跟踪的Task
     *
     * @param waybillStatus
     * @return
     */
    private Task toTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(com.jd.bluedragon.utils.JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }
}
