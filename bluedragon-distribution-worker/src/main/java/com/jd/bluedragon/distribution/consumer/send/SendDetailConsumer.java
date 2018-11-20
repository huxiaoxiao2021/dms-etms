package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.GoodsPrintEsManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
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

    private static final Log logger = LogFactory.getLog(SendDetailConsumer.class);

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Autowired
    private GoodsPrintEsManager goodsPrintEsManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private GoodsPrintService goodsPrintService;

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
            String waybillCode=SerialRuleUtil.getWaybillCode(packageBarCode);
            BaseEntity<BigWaybillDto> baseEntity = getWaybillBaseEntity(waybillCode);
            if (baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                Waybill waybill = baseEntity.getData().getWaybill();
                if (BusinessHelper.isRMA(waybill.getWaybillSign())) {
                    if (!rmaHandOverWaybillService.buildAndStorage(sendDetail, waybill, baseEntity.getData().getGoodsList())) {
                        throw new RuntimeException("[dmsWorkSendDetail消费]存储RMA订单数据失败，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
                    }
                }
                //将运单维度的 托寄物品名是数据 写到es
                //缓存中有 表示不久前，已操作过同运单的包裹，不需要重复写入es了
                String key = Constants.GOODS_PRINT_WAYBILL_STATUS_1+sendDetail.getSendCode() + Constants.SEPARATOR_HYPHEN + waybillCode+Constants.SEPARATOR_HYPHEN+sendDetail.getBoxCode();
                if (!goodsPrintService.getWaybillFromEsOperator(key)) {
                    //缓存中没有有2种情况 1：缓存过期 2：es就一直没存过
                    //查es 是否有该运单
                    GoodsPrintDto goodsPrintDto= goodsPrintEsManager.findGoodsPrintBySendCodeAndWaybillCode(sendDetail.getSendCode(),waybillCode);
                    //es中查不到，就是真没有了， insert该运单
                    if (goodsPrintDto==null){
                        goodsPrintDto = buildGoodsPrintDto(sendDetail, waybill);
                    }else{
                        //如果存在一单分布在不同的箱子里，要把箱号拼起来
                        if (goodsPrintDto.getBoxCode()!=null&&BusinessUtil.isBoxcode(sendDetail.getBoxCode())&&!goodsPrintDto.getBoxCode().contains(sendDetail.getBoxCode())){
                            goodsPrintDto.setBoxCode(goodsPrintDto.getBoxCode()+Constants.SEPARATOR_COMMA+sendDetail.getBoxCode());
                        }else if (goodsPrintDto.getBoxCode()==null&&BusinessUtil.isBoxcode(sendDetail.getBoxCode())){
                            goodsPrintDto.setBoxCode(sendDetail.getBoxCode());
                        }
                        //改为发货状态
                        goodsPrintDto.setSendStatus(Constants.GOODS_PRINT_WAYBILL_STATUS_1);
                    }
                   if (goodsPrintEsManager.insertOrUpdate(goodsPrintDto)){
                       goodsPrintService.setWaybillFromEsOperator(key);
                   }
                }
            } else {
                logger.warn("[dmsWorkSendDetail消费]根据运单号获取运单信息为空，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
            }
        } else {
            logger.warn("[dmsWorkSendDetail消费]无效的运单号/包裹号，packageBarCode:" + packageBarCode + ",boxCode:" + sendDetail.getBoxCode());
        }
    }

    private GoodsPrintDto buildGoodsPrintDto(SendDetailMessage sendDetail, Waybill waybill) {
        GoodsPrintDto goodsPrintDto;
        goodsPrintDto=new GoodsPrintDto();
        goodsPrintDto.setVendorId(waybill.getVendorId());
        BaseStaffSiteOrgDto createSite = this.baseMajorManager
                .getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
        if (createSite!=null){
            goodsPrintDto.setCreateSiteCode(createSite.getSiteCode());
            goodsPrintDto.setCreateSiteName(createSite.getSiteName());
        }
        BaseStaffSiteOrgDto receiveSite = this.baseMajorManager
                .getBaseSiteBySiteId(sendDetail.getReceiveSiteCode());
        if (receiveSite!=null){
            goodsPrintDto.setReceiveSiteCode(receiveSite.getSiteCode());
            goodsPrintDto.setReceiveSiteName(receiveSite.getSiteName());
        }
        goodsPrintDto.setOperateTime(new Date(sendDetail.getOperateTime()));
        if (BusinessUtil.isBoxcode(sendDetail.getBoxCode())){
            sendDetail.setBoxCode(sendDetail.getBoxCode());
        }
        if (waybill.getWaybillExt()!=null&&waybill.getWaybillExt().getConsignWare()!=null){
            goodsPrintDto.setConsignWare(waybill.getWaybillExt().getConsignWare());
        }
        goodsPrintDto.setSendStatus(Constants.GOODS_PRINT_WAYBILL_STATUS_1);
        return goodsPrintDto;
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

}
