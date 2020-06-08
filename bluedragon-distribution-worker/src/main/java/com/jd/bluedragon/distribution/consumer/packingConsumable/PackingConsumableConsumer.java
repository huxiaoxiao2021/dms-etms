package com.jd.bluedragon.distribution.consumer.packingConsumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shipeilin
 * @Description: B网包装耗材消费者：消费运输的MQ：bd_pack_sync_waybill
 * @date 2018年08月15日 16时:10分
 */
@Service("packingConsumableConsumer")
public class PackingConsumableConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    WaybillConsumableRelationService waybillConsumableRelationService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private LogEngine logEngine;

    @JProfiler(jKey = "PackingConsumableConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("PackingConsumableConsumer consume --> 消息Body为【{}】",message.getText());
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.log.warn("PackingConsumableConsumer consume -->消息为空");
            addLog(new WaybillConsumableDto(),startTime);
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("PackingConsumableConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            addLog(new WaybillConsumableDto(),startTime);
            return;
        }
        WaybillConsumableDto packingConsumable = JsonHelper.fromJson(message.getText(), WaybillConsumableDto.class);
        if (packingConsumable == null) {
            this.log.warn("PackingConsumableConsumer consume -->消息转换对象失败：{}" , message.getText());
            addLog(new WaybillConsumableDto(),startTime);
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
        WaybillConsumableRecord oldRecord = waybillConsumableRecordService.queryOneByWaybillCode(packingConsumable.getWaybillCode());
        if(oldRecord != null && oldRecord.getId() != null){
            log.warn("B网包装耗材，重复的运单号：{}" , message.getText());
        }else{
            WaybillConsumableRecord waybillConsumableRecord = convert2WaybillConsumableRecord(packingConsumable);
            if (isNeedConfirmConsumable(packingConsumable)) {
                waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.TREATED_STATE);
            }
            //新增主表
            waybillConsumableRecordService.saveOrUpdate(waybillConsumableRecord);
            //新增明细
            List<WaybillConsumableRelation> waybillConsumableRelationLst = convert2WaybillConsumableRelation(packingConsumable);
            waybillConsumableRelationService.batchAdd(waybillConsumableRelationLst);
        }
        log.debug("PackingConsumableConsumer consume --> 消息消费完成，Body为【{}】",message.getText());
    }

    /**
     * 运单耗材装换:将MQ消息体转换为分拣实体VO
     *
     * @param packingConsumable
     * @return
     */
    private WaybillConsumableRecord convert2WaybillConsumableRecord(WaybillConsumableDto packingConsumable){
        WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
        waybillConsumableRecord.setWaybillCode(packingConsumable.getWaybillCode());
        //根据 packingConsumable.getDmsCode() 查询分拣中心信息
        Integer siteCode = packingConsumable.getDmsCode();
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        waybillConsumableRecord.setDmsId(dto.getSiteCode());
        waybillConsumableRecord.setDmsName(dto.getSiteName());

        //waybillConsumableRecord.setReceiveUserCode(packingConsumable.getOperateUserErp());
        waybillConsumableRecord.setReceiveUserErp(packingConsumable.getOperateUserErp());
        waybillConsumableRecord.setReceiveUserName(packingConsumable.getOperateUserName());
        waybillConsumableRecord.setReceiveTime(DateHelper.parseDateTime(packingConsumable.getOperateTime()));

        waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.UNTREATED_STATE);
        waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.UNTREATED_STATE);

        return waybillConsumableRecord;
    }

    /**
     * 运单耗材明细转换:将MQ消息体转换为分拣实体VO
     * @param packingConsumable
     * @return
     */
    private List<WaybillConsumableRelation> convert2WaybillConsumableRelation(WaybillConsumableDto packingConsumable){
        List<WaybillConsumableDetailDto> packingChargeList = packingConsumable.getPackingChargeList();
        if(packingChargeList == null || packingChargeList.isEmpty()){
            return null;
        }
        Date operateTime = DateHelper.parseDateTime(packingConsumable.getOperateTime());
        List<WaybillConsumableRelation> waybillConsumableRelationLst = new ArrayList<WaybillConsumableRelation>(packingChargeList.size());
        for(WaybillConsumableDetailDto dto : packingChargeList){
            WaybillConsumableRelation relation = new WaybillConsumableRelation();
            relation.setWaybillCode(packingConsumable.getWaybillCode());
            relation.setConsumableCode(dto.getPackingCode());
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

    /* 添加日志 */
    private void addLog(WaybillConsumableDto packingConsumable,long startTime){
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

    /* 判断是否需要直接确认包装耗材，包含木质包装耗材（木架、木箱、木托盘） */
    private boolean isNeedConfirmConsumable(WaybillConsumableDto packingConsumable) {
        if (CollectionUtils.isNotEmpty(packingConsumable.getPackingChargeList())) {
            for(WaybillConsumableDetailDto waybillConsumableDetailDto : packingConsumable.getPackingChargeList()) {
                if(PackingTypeEnum.TY003.getTypeCode().equals(waybillConsumableDetailDto.getPackingType())
                    || PackingTypeEnum.TY004.getTypeCode().equals(waybillConsumableDetailDto.getPackingType())
                    || PackingTypeEnum.TY008.getTypeCode().equals(waybillConsumableDetailDto.getPackingType())) {
                    return false;
                }
            }
        }
        return true;
    }
}
