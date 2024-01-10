package com.jd.bluedragon.distribution.consumer.packingConsumable;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author shipeilin
 * @Description: B网包装耗材消费者：消费运输的MQ：bd_pack_sync_waybill
 * @date 2018年08月15日 16时:10分
 */
@Service("packingConsumableConsumer")
public class PackingConsumableConsumer extends ConsumableConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private LogEngine logEngine;

    @JProfiler(jKey = "PackingConsumableConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("PackingConsumableConsumer consume --> 消息Body为【{}】",message.getText());
        if (Constants.EMPTY_FILL.equals(message.getText()) || null == message.getText()) {
            this.log.warn("PackingConsumableConsumer consume -->消息为空");
            addLog(new WaybillConsumableCommonDto(),startTime);
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("PackingConsumableConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            addLog(new WaybillConsumableCommonDto(),startTime);
            return;
        }
        WaybillConsumableCommonDto packingConsumable = JsonHelper.fromJson(message.getText(), WaybillConsumableCommonDto.class);
        if (packingConsumable == null) {
            this.log.warn("PackingConsumableConsumer consume -->消息转换对象失败：{}" , message.getText());
            addLog(new WaybillConsumableCommonDto(),startTime);
            return;
        }
        if(StringHelper.isEmpty(packingConsumable.getWaybillCode())){
            this.log.warn("PackingConsumableConsumer consume -->消息中没有运单号：{}" , message.getText());
            addLog(packingConsumable,startTime);
            return;
        }
        if(packingConsumable.getDmsCode() == null){
            this.log.warn("PackingConsumableConsumer consume -->消息中没有站点编号：{}" , message.getText());
            addLog(packingConsumable,startTime);
            return;
        }
        if(checkColdWaybill(packingConsumable)){
            this.log.warn("PackingConsumableConsumer consume -->冷链条线耗材无须关注：{}" , message.getText());
            addLog(packingConsumable,startTime);
            return;
        }
        try {
            handleWaybillConsumableAndRelation(packingConsumable);
        } catch (JyBizException e) {
            log.warn("B网包装耗材消费,{}", e.getMessage());
            throw new JyBizException(e.getMessage());
        } catch (Exception e) {
            log.error("B网包装耗材消费出现异常:waybillCode={}", packingConsumable.getWaybillCode(), e);
        }
        log.debug("PackingConsumableConsumer consume --> 消息消费完成，Body为【{}】",message.getText());
    }

    /**
     * 检查冷链产品
     */
    private boolean checkColdWaybill(WaybillConsumableCommonDto packingConsumable){
        String productTypeStr = packingConsumable.getProductType();
        if (StringUtils.isNotBlank(productTypeStr)) {
            List<String> productTypeList = Arrays.asList(productTypeStr.split(Constants.SEPARATOR_COMMA));
            if (productTypeList.contains(Constants.PRODUCT_TYPE_MEDICAL_PART_BILL)
                || productTypeList.contains(Constants.PRODUCT_TYPE_MEDICINE_DP)
                || productTypeList.contains(Constants.PRODUCT_TYPE_MEDICAL_COLD_BILL)) {
                return true;
            }
        }
        return false;
    }


    public WaybillConsumableRecord convert2WaybillConsumableRecord(WaybillConsumableCommonDto packingConsumable){
        WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
        waybillConsumableRecord.setWaybillCode(packingConsumable.getWaybillCode());
        // 根据packingConsumable.getDmsCode()查询分拣中心信息
        Integer siteCode = packingConsumable.getDmsCode();
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        waybillConsumableRecord.setDmsId(dto.getSiteCode());
        waybillConsumableRecord.setDmsName(dto.getSiteName());

        //waybillConsumableRecord.setReceiveUserCode(packingConsumable.getOperateUserErp());
        waybillConsumableRecord.setReceiveUserErp(packingConsumable.getOperateUserErp());
        waybillConsumableRecord.setReceiveUserName(packingConsumable.getOperateUserName());
        waybillConsumableRecord.setReceiveTime(DateHelper.parseDateTime(packingConsumable.getOperateTime()));

        waybillConsumableRecord.setConfirmStatus(Constants.NUMBER_ZERO);
        waybillConsumableRecord.setModifyStatus(Constants.NUMBER_ZERO);

        return waybillConsumableRecord;
    }


    public List<WaybillConsumableRelation> convert2WaybillConsumableRelation(WaybillConsumableCommonDto packingConsumable){
        List<WaybillConsumableDetailDto> packingChargeList = packingConsumable.getPackingChargeList();
        if (packingChargeList == null || packingChargeList.isEmpty()) {
            return null;
        }
        Date operateTime = DateHelper.parseDateTime(packingConsumable.getOperateTime());
        List<WaybillConsumableRelation> waybillConsumableRelationLst = new ArrayList<>(packingChargeList.size());
        for (WaybillConsumableDetailDto dto : packingChargeList) {
            WaybillConsumableRelation relation = new WaybillConsumableRelation();
            relation.setWaybillCode(packingConsumable.getWaybillCode());
            relation.setConsumableCode(dto.getPackingCode());

            relation.setConsumableName(dto.getPackingName());
            relation.setConsumableType(dto.getPackingType());
            relation.setConsumableTypeName(dto.getPackingTypeName());
            relation.setSpecification(dto.getPackingSpecification());
            relation.setUnit(dto.getPackingUnit());
            if (dto.getPackingVolume() != null){
                relation.setVolume(BigDecimal.valueOf(dto.getPackingVolume() * 100 * 100 * 100));
            }
            if (dto.getVolumeCoefficient() != null) {
                relation.setVolumeCoefficient(BigDecimal.valueOf(dto.getVolumeCoefficient()));
            }
//            relation.setWeight(dto.get);
            if (dto.getPackingCharge() != null) {
                relation.setPackingCharge(BigDecimal.valueOf(dto.getPackingCharge()));
            }

            relation.setReceiveQuantity(dto.getPackingNumber());
            relation.setConfirmQuantity(dto.getPackingNumber());
            //relation.setOperateUserCode(packingConsumable.getOperateUserErp());
            String erp = packingConsumable.getOperateUserErp();
            erp = StringUtils.isBlank(erp) ? "" : erp;
            relation.setOperateUserErp(erp);
            relation.setOperateTime(operateTime);
            waybillConsumableRelationLst.add(relation);
        }

        return waybillConsumableRelationLst;
    }

    /**
     * 添加日志
     */
    private void addLog(WaybillConsumableCommonDto packingConsumable, long startTime){
        long endTime = System.currentTimeMillis();
        JSONObject request = new JSONObject();
        request.put("waybillCode", packingConsumable.getWaybillCode());
        request.put("siteCode", packingConsumable.getDmsCode());
        request.put("operateUserName", packingConsumable.getOperateUserName());
        request.put("operateUserErp", packingConsumable.getOperateUserErp());

        JSONObject response = new JSONObject();

        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.CONSUMABLE_RECORD_CONSUME_FAIL)
                .processTime(endTime,startTime)
                .operateRequest(request)
                .operateResponse(response)
                .methodName("PackingConsumableConsumer#consume")
                .build();
        logEngine.addLog(businessLogProfiler);
    }


    public boolean isNeedConfirmConsumable(WaybillConsumableCommonDto packingConsumable) {
        if (CollectionUtils.isNotEmpty(packingConsumable.getPackingChargeList())) {
            for (WaybillConsumableDetailDto waybillConsumableDetailDto : packingConsumable.getPackingChargeList()) {
                if (ConsumableCodeEnums.isWoodenConsumable(waybillConsumableDetailDto.getPackingCode())
                        || PackingTypeEnum.isWoodenConsumable(waybillConsumableDetailDto.getPackingType())) {
                    return false;
                }
            }
        }
        return true;
    }
}
