package com.jd.bluedragon.distribution.consumer.packingConsumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumer.packingConsumable
 * @ClassName: CPackingConsumableConsumer
 * @Description: 快递包装耗材消费者：消费终端的MQ：qlerp_receive_info
 * @Author： wuzuxiang
 * @CreateDate 2022/2/7 10:50
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Component("cPackingConsumableConsumer")
@Slf4j
public class CPackingConsumableConsumer extends MessageBaseConsumer {

    /**
     * 快递包装耗材锁前缀
     */
    public static final String CPACK_CONSUMABLE_LOCK_PREFIX = "CPACK_CONSUMABLE_LOCK_";

    private final static int LOCK_TIME = 60;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillConsumableRelationService waybillConsumableRelationService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    @JProfiler(jKey = "CPackingConsumableConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void consume(Message message) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("消费快递侧揽收后的包装耗材消息：{}", message.getText());
        }
        ReceivePackingConsumableDto packingConsumableDto = JsonHelper.fromJson(message.getText(), ReceivePackingConsumableDto.class);
        if (packingConsumableDto == null) {
            log.error("消费快递侧揽收后消息，反序列化为空，{}", message.getText());
            return;
        }

        if (StringUtils.isBlank(packingConsumableDto.getWaybillCode())) {
            log.warn("消费快递侧揽收后消息，运单号为空，{}", message.getText());
            return;
        }

        if (!WaybillUtil.isWaybillCode(packingConsumableDto.getWaybillCode())) {
            log.warn("消费快递侧揽收后消息，运单号不符合单号规则，消息丢弃，{}", message.getText());
            return;
        }

        if (packingConsumableDto.getDmsCode() == null) {
            log.warn("消费快递侧揽收后消息，分拣中心编号为空，{}", message.getText());
            return;
        }

        //包裹维度的数据，但是运单上所有的包装耗材信息会在某一个包裹维度的消息中发出
        if (CollectionUtils.isEmpty(packingConsumableDto.getBoxChargeDetails())) {
            log.warn("消费快递侧揽收后消息，无包装耗材信息，{}", message.getText());
            return;
        }

        //如果耗材编码或者耗材类型为空，则说明可能该消息是未发版的老流程走过来的数据，此数据不在这个消费中承接
        if (StringUtils.isEmpty(packingConsumableDto.getBoxChargeDetails().get(0).getBarCode())
                || StringUtils.isEmpty(packingConsumableDto.getBoxChargeDetails().get(0).getMaterialTypeCode())) {
            log.warn("消费快递侧揽收后消息，无包装耗材编码或者耗材类型可能为老流程数据，丢弃该消息，{}", message.getText());
            return;
        }

        
        /* 处理终端的entryId获取员工erp信息 */
        if (packingConsumableDto.getEntryId() != null) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByStaffId(packingConsumableDto.getEntryId());
            if (null != baseStaffSiteOrgDto) {
                packingConsumableDto.setEntryErp(baseStaffSiteOrgDto.getErp());
            }
        }

        try {
            // 运单维度加锁
            if (!lock(packingConsumableDto.getWaybillCode())) {
                throw new JyBizException("快递包装耗材消费获取锁失败");
            }

            /* 保存主表 */
            WaybillConsumableRecord waybillConsumableRecord = waybillConsumableRecordService.convert2WaybillConsumableRecord(packingConsumableDto);
            if (isNeedConfirmConsumable(packingConsumableDto)) {
                waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.TREATED_STATE);
                waybillConsumableRecord.setConfirmUserErp(waybillConsumableRecord.getReceiveUserErp());//此处是操作人id，来源于终端的entryId
                waybillConsumableRecord.setConfirmUserName(waybillConsumableRecord.getReceiveUserName());
            }
            WaybillConsumableRecord oldRecord = waybillConsumableRecordService.queryOneByWaybillCode(packingConsumableDto.getWaybillCode());
            if (oldRecord == null) {
                waybillConsumableRecordService.saveOrUpdate(waybillConsumableRecord);
            } else {
                waybillConsumableRecordService.updateByCondition(waybillConsumableRecord);
            }

            /* 保存明细表 */
            List<WaybillConsumableRelation> waybillConsumableRelationLst = waybillConsumableRelationService.convert2WaybillConsumableRelation(packingConsumableDto);
            if (CollectionUtils.isNotEmpty(waybillConsumableRelationLst)) {
                for (WaybillConsumableRelation relation : waybillConsumableRelationLst) {
                    WaybillConsumableRelation oldRelation = waybillConsumableRelationService.findByWaybillCodeAndConsumableCode(relation);
                    if (oldRelation == null) {
                        waybillConsumableRelationService.saveOrUpdate(relation);
                    } else {
                        if (relation.getReceiveQuantity() != null && relation.getReceiveQuantity() > 0) {
                            waybillConsumableRelationService.updateByWaybillCodeAndConsumableCode(relation);
                        }
                    }
                }
            }
        } finally {
            unLock(packingConsumableDto.getWaybillCode());
        }
    }


    /*
        判断是否需要直接确认包装耗材，包含木质包装耗材（木架、木箱、木托盘）
        终端包装耗材融合项目：对于木质包装耗材的判断标准有变，以终端的木质耗材编码为准，分拣侧写死在枚举中ConsumableCodeEnums，通过isWoodenConsumable进行判断是否是木质
     */
    private boolean isNeedConfirmConsumable(ReceivePackingConsumableDto packingConsumableDto) {
        if (CollectionUtils.isNotEmpty(packingConsumableDto.getBoxChargeDetails())) {
            for(BoxChargeDetail waybillConsumableDetailDto : packingConsumableDto.getBoxChargeDetails()) {
                if(PackingTypeEnum.isWoodenConsumable(waybillConsumableDetailDto.getMaterialTypeCode())
                        || ConsumableCodeEnums.isWoodenConsumable(waybillConsumableDetailDto.getBarCode())

                ) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean lock(String waybillCode) {
        String lockKey = CPACK_CONSUMABLE_LOCK_PREFIX + waybillCode;
        log.info("快递包装耗材消费开始获取锁lockKey={}", lockKey);
        try {
            if (!jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS)) {
                Thread.sleep(100);
                return jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, LOCK_TIME, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("快递包装耗材消费lock异常:sourceBizId={},e=", waybillCode, e);
            jimdbCacheService.del(lockKey);
        }
        return true;
    }

    private void unLock(String waybillCode) {
        try {
            String lockKey = CPACK_CONSUMABLE_LOCK_PREFIX + waybillCode;
            jimdbCacheService.del(lockKey);
        } catch (Exception e) {
            log.error("快递包装耗材消费unLock异常:waybillCode={},e=", waybillCode, e);
        }
    }

}
