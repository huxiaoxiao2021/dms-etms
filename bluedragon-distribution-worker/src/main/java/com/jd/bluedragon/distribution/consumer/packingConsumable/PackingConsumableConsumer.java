package com.jd.bluedragon.distribution.consumer.packingConsumable;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.MessageFormat;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    WaybillConsumableRelationService waybillConsumableRelationService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @JProfiler(jKey = "PackingConsumableConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        logger.debug("PackingConsumableConsumer consume --> 消息Body为【" + message.getText() + "】");
        if (message == null || "".equals(message.getText()) || null == message.getText()) {
            this.logger.warn("PackingConsumableConsumer consume -->消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("PackingConsumableConsumer consume -->消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        PackingWaybillInfoVo packingConsumable = JsonHelper.fromJson(message.getText(),PackingWaybillInfoVo.class);
        if (packingConsumable == null) {
            this.logger.error("PackingConsumableConsumer consume -->消息转换对象失败：" + message.getText());
            return;
        }
        if(StringHelper.isEmpty(packingConsumable.getWaybillCode())){
            this.logger.error("PackingConsumableConsumer consume -->消息中没有运单号：" + message.getText());
            return;
        }
        WaybillConsumableRecord oldRecord = waybillConsumableRecordService.queryOneByWaybillCode(packingConsumable.getWaybillCode());
        if(oldRecord != null && oldRecord.getId() != null){
            logger.warn("B网包装耗材，重复的运单号：" + message.getText());
        }else{
            WaybillConsumableRecord waybillConsumableRecord = convert2WaybillConsumableRecord(packingConsumable);
            //新增主表
            waybillConsumableRecordService.saveOrUpdate(waybillConsumableRecord);
            //新增明细
            List<WaybillConsumableRelation> waybillConsumableRelationLst = convert2WaybillConsumableRelation(packingConsumable);
            waybillConsumableRelationService.batchAdd(waybillConsumableRelationLst);
        }
        logger.debug("PackingConsumableConsumer consume --> 消息消费完成，Body为【" + message.getText() + "】");
    }

    /**
     * 运单耗材装换
     * @param packingConsumable
     * @return
     */
    private WaybillConsumableRecord convert2WaybillConsumableRecord(PackingWaybillInfoVo packingConsumable){
        WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
        waybillConsumableRecord.setWaybillCode(packingConsumable.getWaybillCode());
        //根据 packingConsumable.getDmsCode() 查询分拣中心信息
        String siteCode = packingConsumable.getDmsCode();
        BaseStaffSiteOrgDto dto = null;
        if(NumberHelper.isNumber(siteCode)){
            dto = baseMajorManager.getBaseSiteBySiteId(Integer.parseInt(siteCode));
        }else {
            dto = baseMajorManager.getBaseSiteByDmsCode(siteCode);
        }
        waybillConsumableRecord.setDmsId(dto.getSiteCode());
        waybillConsumableRecord.setDmsName(dto.getSiteName());

        //waybillConsumableRecord.setReceiveUserCode(packingConsumable.getOperateUserErp());
        waybillConsumableRecord.setReceiveUserErp(packingConsumable.getOperateUserErp());
        waybillConsumableRecord.setReceiveUserName(packingConsumable.getOperateUserName());
        waybillConsumableRecord.setReceiveTime(DateHelper.parseDateTime(packingConsumable.getOperatorTime()));

        waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.UNTREATED_STATE);
        waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.UNTREATED_STATE);

        return waybillConsumableRecord;
    }

    /**
     * 运单耗材明细转换
     * @param packingConsumable
     * @return
     */
    private List<WaybillConsumableRelation> convert2WaybillConsumableRelation(PackingWaybillInfoVo packingConsumable){
        List<PackingInfoDto> packingChargeList = packingConsumable.getPackingChargeList();
        if(packingChargeList == null || packingChargeList.isEmpty()){
            return null;
        }
        Date OperateTime = DateHelper.parseDateTime(packingConsumable.getOperatorTime());
        List<WaybillConsumableRelation> waybillConsumableRelationLst = new ArrayList<WaybillConsumableRelation>(packingChargeList.size());
        for(PackingInfoDto dto : packingChargeList){
            WaybillConsumableRelation relation = new WaybillConsumableRelation();
            relation.setWaybillCode(packingConsumable.getWaybillCode());
            relation.setConsumableCode(dto.getPackingCode());
            relation.setReceiveQuantity(dto.getConsumeNumber());
            relation.setConfirmQuantity(dto.getConsumeNumber());
            //relation.setOperateUserCode(packingConsumable.getOperateUserErp());
            String erp = packingConsumable.getOperateUserErp();
            erp = StringUtils.isBlank(erp) ? "" : erp;
            relation.setOperateUserErp(erp);
            relation.setOperateTime(OperateTime);
            waybillConsumableRelationLst.add(relation);
        }

        return waybillConsumableRelationLst;
    }

    static class PackingWaybillInfoVo implements Serializable {

        private static final long serialVersionUID = 1L;

        private String waybillCode;//运单号不为空

        private String dmsCode;//分拣中心编码

        private String operateUserErp;//操作人账号

        private String operateUserName;//操作人姓名

        private String operatorTime;//操作时间

        private List<PackingInfoDto> packingChargeList;//耗材明细


        public String getWaybillCode() {
            return waybillCode;
        }

        public void setWaybillCode(String waybillCode) {
            this.waybillCode = waybillCode;
        }

        public String getDmsCode() {
            return dmsCode;
        }

        public void setDmsCode(String dmsCode) {
            this.dmsCode = dmsCode;
        }

        public String getOperateUserErp() {
            return operateUserErp;
        }

        public void setOperateUserErp(String operateUserErp) {
            this.operateUserErp = operateUserErp;
        }

        public String getOperateUserName() {
            return operateUserName;
        }

        public void setOperateUserName(String operateUserName) {
            this.operateUserName = operateUserName;
        }

        public String getOperatorTime() {
            return operatorTime;
        }

        public void setOperatorTime(String operatorTime) {
            this.operatorTime = operatorTime;
        }

        public List<PackingInfoDto> getPackingChargeList() {
            return packingChargeList;
        }

        public void setPackingChargeList(List<PackingInfoDto> packingChargeList) {
            this.packingChargeList = packingChargeList;
        }


    }
    static  class PackingInfoDto  implements Serializable {

        private static final long serialVersionUID = 1L;
        //耗材编号
        private String packingCode;
        //耗材名称
        private String packingName;
        //耗材类型
        private String packingType;
        //耗材体积
        private Double packingVolume;
        //体积系数
        private Double volumeCoefficient;
        //包装规格
        private String packingSpecification;
        //耗材单位
        private String packingUnit;
        //耗材数量
        private Integer consumeNumber;

        public String getPackingCode() {
            return packingCode;
        }

        public void setPackingCode(String packingCode) {
            this.packingCode = packingCode;
        }

        public String getPackingName() {
            return packingName;
        }

        public void setPackingName(String packingName) {
            this.packingName = packingName;
        }

        public String getPackingType() {
            return packingType;
        }

        public void setPackingType(String packingType) {
            this.packingType = packingType;
        }

        public Double getPackingVolume() {
            return packingVolume;
        }

        public void setPackingVolume(Double packingVolume) {
            this.packingVolume = packingVolume;
        }

        public Double getVolumeCoefficient() {
            return volumeCoefficient;
        }

        public void setVolumeCoefficient(Double volumeCoefficient) {
            this.volumeCoefficient = volumeCoefficient;
        }

        public String getPackingSpecification() {
            return packingSpecification;
        }

        public void setPackingSpecification(String packingSpecification) {
            this.packingSpecification = packingSpecification;
        }

        public String getPackingUnit() {
            return packingUnit;
        }

        public void setPackingUnit(String packingUnit) {
            this.packingUnit = packingUnit;
        }

        public Integer getConsumeNumber() {
            return consumeNumber;
        }

        public void setConsumeNumber(Integer consumeNumber) {
            this.consumeNumber = consumeNumber;
        }
    }

}
