package com.jd.bluedragon.distribution.consumer.packingConsumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableCommonDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 包装耗材消费者通用逻辑抽象类
 */
public abstract class ConsumableConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(ConsumableConsumer.class);

    /**
     * 快递包装耗材锁前缀
     */
    public static final String WAYBILL_CONSUMABLE_LOCK_PREFIX = "waybill:consumable:lock:";

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillConsumableRelationService waybillConsumableRelationService;

    /**
     * 处理耗材及耗材明细信息
     */
    @JProfiler(jKey = "ConsumableConsumer.handleWaybillConsumableAndRelation", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void handleWaybillConsumableAndRelation(WaybillConsumableCommonDto waybillConsumableCommonDto) {
        // 运单号
        String waybillCode = waybillConsumableCommonDto.getWaybillCode();
        // 互斥锁
        String mutexKey = WAYBILL_CONSUMABLE_LOCK_PREFIX + waybillCode;
        try {
            // 运单维度加锁防止并发
            if (!redisClientOfJy.set(mutexKey, Constants.EMPTY_FILL, Constants.CONSTANT_NUMBER_ONE, TimeUnit.MINUTES, false)) {
                String warnMsg = String.format("运单号:%s-包装耗材消费正在处理中!", waybillCode);
                throw new JyBizException(warnMsg);
            }

            // 保存主表
            WaybillConsumableRecord waybillConsumableRecord = convert2WaybillConsumableRecord(waybillConsumableCommonDto);
            if (isNeedConfirmConsumable(waybillConsumableCommonDto)) {
                waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.TREATED_STATE);
                waybillConsumableRecord.setConfirmUserErp(waybillConsumableRecord.getReceiveUserErp());//此处是操作人id，来源于终端的entryId
                waybillConsumableRecord.setConfirmUserName(waybillConsumableRecord.getReceiveUserName());
            }
            WaybillConsumableRecord oldRecord = waybillConsumableRecordService.queryOneByWaybillCode(waybillCode);
            if (oldRecord == null) {
                waybillConsumableRecordService.saveOrUpdate(waybillConsumableRecord);
            } else {
                waybillConsumableRecordService.updateByCondition(waybillConsumableRecord);
            }

            // 保存明细表
            List<WaybillConsumableRelation> waybillConsumableRelationLst = convert2WaybillConsumableRelation(waybillConsumableCommonDto);
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
            redisClientOfJy.del(mutexKey);
        }
    }

    /*
     * 判断是否需要直接确认包装耗材，包含木质包装耗材（木架、木箱、木托盘）
     * 终端包装耗材融合项目：对于木质包装耗材的判断标准有变，以终端的木质耗材编码为准，分拣侧写死在枚举中ConsumableCodeEnums，通过isWoodenConsumable进行判断是否是木质
     */
    public boolean isNeedConfirmConsumable(WaybillConsumableCommonDto waybillConsumableCommonDto) {
        return false;
    }

    /**
     * 运单耗材转换:将MQ消息体转换为分拣实体VO
     */
    public WaybillConsumableRecord convert2WaybillConsumableRecord(WaybillConsumableCommonDto waybillConsumableCommonDto) {
        return null;
    }

    /**
     * 运单耗材明细转换:将MQ消息体转换为分拣实体VO
     */
    public List<WaybillConsumableRelation> convert2WaybillConsumableRelation(WaybillConsumableCommonDto waybillConsumableCommonDto) {
        return null;
    }

}
