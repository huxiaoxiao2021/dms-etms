package com.jd.bluedragon.distribution.consumer.packingConsumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class CPackingConsumableConsumer extends ConsumableConsumer {

    @Autowired
    private BaseMajorManager baseMajorManager;


    @Override
    @JProfiler(jKey = "CPackingConsumableConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void consume(Message message) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("消费快递侧揽收后的包装耗材消息：{}", message.getText());
        }
        WaybillConsumableCommonDto packingConsumableDto = JsonHelper.fromJson(message.getText(), WaybillConsumableCommonDto.class);
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
            handleWaybillConsumableAndRelation(packingConsumableDto);
        } catch (JyBizException e) {
            log.warn("快递包装耗材消费,{}", e.getMessage());
            throw new JyBizException(e.getMessage());
        } catch (Exception e) {
            log.error("快递包装耗材消费出现异常:waybillCode={}", packingConsumableDto.getWaybillCode(), e);
        }
    }



    @Override
    public boolean isNeedConfirmConsumable(WaybillConsumableCommonDto consumableDto) {
        if (CollectionUtils.isNotEmpty(consumableDto.getBoxChargeDetails())) {
            for (BoxChargeDetail waybillConsumableDetailDto : consumableDto.getBoxChargeDetails()) {
                if (PackingTypeEnum.isWoodenConsumable(waybillConsumableDetailDto.getMaterialTypeCode())
                        || ConsumableCodeEnums.isWoodenConsumable(waybillConsumableDetailDto.getBarCode())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public WaybillConsumableRecord convert2WaybillConsumableRecord(WaybillConsumableCommonDto consumableDto) {
        if (consumableDto == null) {
            return null;
        }
        WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
        waybillConsumableRecord.setWaybillCode(consumableDto.getWaybillCode());
        // 根据 packingConsumable.getDmsCode() 查询分拣中心信息
        Integer siteCode = consumableDto.getDmsCode();
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        waybillConsumableRecord.setDmsId(dto.getSiteCode());
        waybillConsumableRecord.setDmsName(dto.getSiteName());
        waybillConsumableRecord.setReceiveUserErp(consumableDto.getEntryErp());
        waybillConsumableRecord.setReceiveUserCode(String.valueOf(consumableDto.getEntryId()));
        waybillConsumableRecord.setReceiveUserName(consumableDto.getEntryName());
        waybillConsumableRecord.setReceiveTime(DateHelper.toDate(consumableDto.getPdaTime()));
        waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.UNTREATED_STATE);
        waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.UNTREATED_STATE);
        return waybillConsumableRecord;
    }

    @Override
    public List<WaybillConsumableRelation> convert2WaybillConsumableRelation(WaybillConsumableCommonDto packingConsumableDto) {
        List<BoxChargeDetail> boxChargeDetails = packingConsumableDto.getBoxChargeDetails();
        if (CollectionUtils.isEmpty(boxChargeDetails)) {
            return null;
        }
        Date operateTime = DateHelper.toDate(packingConsumableDto.getPdaTime());
        String erp = StringUtils.isEmpty(packingConsumableDto.getEntryErp())? String.valueOf(packingConsumableDto.getEntryId()) : packingConsumableDto.getEntryErp();
        List<WaybillConsumableRelation> waybillConsumableRelationLst = new ArrayList<>(boxChargeDetails.size());
        for (BoxChargeDetail boxChargeDetail : boxChargeDetails) {
            WaybillConsumableRelation relation = new WaybillConsumableRelation();
            relation.setWaybillCode(packingConsumableDto.getWaybillCode());
            relation.setConsumableCode(boxChargeDetail.getBarCode());
            relation.setConsumableName(boxChargeDetail.getBoxName());
            relation.setConsumableType(boxChargeDetail.getMaterialTypeCode());
            relation.setConsumableTypeName(boxChargeDetail.getMaterialTypeName());
            relation.setSpecification(boxChargeDetail.getMaterialSpecification());
            relation.setUnit(boxChargeDetail.getMaterialUnit());
            if (boxChargeDetail.getMaterialVolume() != null) {
                relation.setVolume(BigDecimal.valueOf(boxChargeDetail.getMaterialVolume() * 100 * 100 * 100));
            }
            if (boxChargeDetail.getVolumeCoefficient() != null) {
                relation.setVolumeCoefficient(BigDecimal.valueOf(boxChargeDetail.getVolumeCoefficient()));
            }
            relation.setPackingCharge(boxChargeDetail.getMaterialAmount());
            if (boxChargeDetail.getMaterialNumber() != null) {
                relation.setReceiveQuantity(boxChargeDetail.getMaterialNumber().doubleValue());
                relation.setConfirmQuantity(boxChargeDetail.getMaterialNumber().doubleValue());
            }
            relation.setOperateUserErp(erp);
            relation.setOperateTime(operateTime);
            waybillConsumableRelationLst.add(relation);
        }
        return waybillConsumableRelationLst;
    }

}
