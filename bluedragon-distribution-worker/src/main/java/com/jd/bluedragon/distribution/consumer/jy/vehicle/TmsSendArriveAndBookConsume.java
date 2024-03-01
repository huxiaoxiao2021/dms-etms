package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.inspection.JyTrustHandoverAutoInspectionService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 运输系统-围栏到车后发送包裹到达消息
 * 文档：https://cf.jd.com/pages/viewpage.action?pageId=364760234
 * @Author zhengchengfa
 * @Date 2024/2/29 14:58
 * @Description
 */
@Service
public class TmsSendArriveAndBookConsume extends MessageBaseConsumer {

    private Logger logger = LoggerFactory.getLogger(TmsSendArriveAndBookConsume.class);

    @Autowired
    private JyTrustHandoverAutoInspectionService jyTrustHandoverAutoInspectionService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, objects);
        }
    }
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.TmsSendArriveAndBookConsume.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("TmsSendArriveAndBookConsume consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("TmsSendArriveAndBookConsume consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        TmsSendArriveAndBookConsume.TmsSendArriveAndBookMqBody mqBody = JsonHelper.fromJson(message.getText(), TmsSendArriveAndBookConsume.TmsSendArriveAndBookMqBody.class);
        if(mqBody == null){
            logger.error("TmsSendArriveAndBookConsume consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        //无效数据过滤
        if(!invalidDataFilter(mqBody)) {
            return;
        }
        logInfo("运输围栏到车包裹到达消息开始消费，mqBody={}", message.getText());
        try{
            PackageArriveAutoInspectionDto packageArriveAutoInspectionDto = this.convertPackageArriveAutoInspectionDto(mqBody);
            jyTrustHandoverAutoInspectionService.packageArriveAndAutoInspection(packageArriveAutoInspectionDto);
        }catch (Exception ex) {
            logger.error("运输围栏到车包裹到达消息消费异常，businessId={},errMsg={},content={}");
            throw new JyBizException("运输围栏到车包裹到达消息消费异常,businessId=" + message.getBusinessId());
        }
        logInfo("运输围栏到车包裹到达消息消费成功，内容{}", message.getText());

    }

    private PackageArriveAutoInspectionDto convertPackageArriveAutoInspectionDto(TmsSendArriveAndBookMqBody mqBody) {
        PackageArriveAutoInspectionDto dto = new PackageArriveAutoInspectionDto();
        dto.setTransBookCode(mqBody.getTransBookCode());
        dto.setPackageCode(mqBody.getPackageCode());
        dto.setWaybillCode(StringUtils.isNotBlank(mqBody.getWaybillCode()) ? mqBody.getWaybillCode() : WaybillUtil.getWaybillCode(mqBody.getPackageCode()));
        dto.setTransWorkCode(mqBody.getTransWorkCode());
        dto.setTransWorkItemCode(mqBody.getTransWorkItemCode());
        dto.setArriveSiteCode(mqBody.getEndNodeCode());

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteByDmsCode(mqBody.getEndNodeCode());
        if(Objects.isNull(baseSite) || Objects.isNull(baseSite.getSiteCode())) {
            logger.error("基础资料获取围栏到车场地失败");
            throw new JyBizException(String.format("包裹%s围栏到车场地%s基础资料获取失败", mqBody.getPackageCode(), mqBody.getEndNodeCode()));
        }
        if(StringUtils.isBlank(baseSite.getSiteName())) {
            logWarn("围栏到车包裹到达场地名称为空，packageCode={},到达场地={}，基础资料返回={}", mqBody.getPackageCode(), mqBody.getEndNodeCode(), JsonHelper.toJson(baseSite));
        }
        dto.setArriveSiteId(baseSite.getSiteCode());
        dto.setArriveSiteName(StringUtils.isBlank(baseSite.getSiteName()) ? StringUtils.EMPTY : baseSite.getSiteName());
        dto.setCreateTime(mqBody.getCreateTime());
        dto.setOperateTime(mqBody.getOperateTime());
        dto.setFirstConsumerTime(System.currentTimeMillis());
        return dto;
    }


    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(TmsSendArriveAndBookConsume.TmsSendArriveAndBookMqBody mqBody) {
        if(StringUtils.isBlank(mqBody.getTransBookCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，transBookCode【委托书编码:TMS系统全局唯一】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(WaybillUtil.isPackageCode(mqBody.getPackageCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，packageCode非法，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getTransWorkCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，TransWorkCode【派车任务编码】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getTransWorkItemCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，TransWorkItemCode【派车任务明细编码】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(StringUtils.isBlank(mqBody.getEndNodeCode())) {
            logInfo("运输围栏到车包裹到达消息过滤，EndNodeCode【到达场地】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getOperateTime())) {
            logInfo("运输围栏到车包裹到达消息过滤，OperateTime【操作时间】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        if(Objects.isNull(mqBody.getCreateTime())) {
            logInfo("运输围栏到车包裹到达消息过滤，createTime【下发时间】为空，mqBody={}", JsonHelper.toJson(mqBody));
            return false;
        }
        return true;
    }


    /**
     * {
     *     "areaArriveCarTime": "2024-02-27 13:38:52",
     *     "areaArriveImei": "F213A220447400000",
     *     "areaImeiFlag": 1,
     *     "areaSendArriveMatchFlag": 1,
     *     "areaSendCarTime": "2024-02-27 13:33:32",
     *     "areaSendImei": "F213A220447400000",
     *     "beginNodeCode": "731F001",
     *     "beginNodeName": "长沙望城散货分拣中心",
     *     "billCode": "2049-1363328-20240227132476153",
     *     "billType": 1,
     *     "businessType": 14,
     *     "carrierCode": "0000000",
     *     "carrierDriverCode": "dengtianjun",
     *     "carrierDriverName": "邓田军",
     *     "carrierDriverPhone": "17347212148",
     *     "carrierName": "京东自营车队",
     *     "createTime": "2024-02-27 13:38:52",
     *     "endNodeCode": "731Y222",
     *     "endNodeName": "长沙高星营业部",
     *     "operateTime": "2024-02-27 13:38:51",
     *     "packageCode": "JD0135508820873-1-1-",
     *     "planArriveTime": "2024-02-27 06:10:00",
     *     "planDepartTime": "2024-02-27 05:30:00",
     *     "routeLineCode": "R2202227388811",
     *     "sealCarCode": "SC24022783286780",
     *     "sendArriveType": 200,
     *     "sendCarInArea": 1,
     *     "sendCarTime": "2024-02-27 13:31:47",
     *     "sendImei": "531b94ec-21e1-4781-b9be-e1abef4fc8fb-72-8",
     *     "transBookCode": "TB24022796456878",
     *     "transJobCode": "TJ24022739122553",
     *     "transJobItemCode": "TJ24022739122553-001",
     *     "transType": 11,
     *     "transWorkCode": "TW24022797520477",
     *     "transWorkItemCode": "TW24022797520477-001",
     *     "transportCode": "R2202227388811",
     *     "vehicleNumber": "湘AA27393",
     *     "waybillCode": "JD0135508820873"
     * }
     */
    private class TmsSendArriveAndBookMqBody implements Serializable {
        static final long serialVersionUID = 1L;
        //委托书编码	: TMS系统全局唯一
        private String transBookCode;
        //运输任务编码
        private String transJobCode;
        //运输任务项编码
        private String transJobItemCode;
        //派车任务编码
        private String transWorkCode;
        //派车任务项编码
        private String transWorkItemCode;
        //车牌号
        private String vehicleNumber;
        //业务类型
        private Integer businessType;
        //封车号
        private String sealCarCode;
        //单据编号
        private String billCode;
        //1批次号，7转运批次号，5大件包裹号，13大件内配单号，其它，20-多联包裹
        private Integer billType;
        //发车操作是否在围栏内: 1-在围栏内,2-不在围栏内,-1-条件不足，无法判断
        private Integer sendCarInArea;
        //发车时间
        private Date sendCarTime;
        //到车时间
        private Date arriveCarTime;
        //到车操作是否在围栏内: 1-在围栏内,2-不在围栏内,-1-条件不足，无法判断
        private Integer arriveCarInArea;
        private String waybillCode;
        private String packageCode;
        //承运商编号
        private String carrierCode;
        //承支商名称
        private String carrierName;
        //计划发车时间
        private Date planDepartTime;
        //计划到达时间
        private Date planArriveTime;
        //网点编号	 来自运输基础资料
        private String beginNodeCode;
        //网点名称
        private String beginNodeName;
        //网点编号  来自运输基础资料
        private String endNodeCode;
        //网点名称
        private String endNodeName;
        /**
         * 操作类型	: 1是发车，2是到车 99:委托书;100:围栏发车，200：围栏到车，10-铁路发车，20-铁路到车，30-发货登记
         */
        private Integer sendArriveType;
        //操作人编码
        private String operateUserCode;
        private String operateUserName;
        //操作时间
        private Date operateTime;
        //运力编码
        private String transportCode;
        //线路编码
        private String routeLineCode;
        // 发到车是否匹配，1是，0否;
        private Integer sendArriveMatchFlag;
        //围栏发车
        private Date areaSendCarTime;
        //围栏到车
        private Date areaArriveCarTime;
        // 发到车是否匹配，1是，0否;
        private Integer areaSendArriveMatchFlag;
        //司机编号
        private String carrierDriverCode;
        //司机姓名
        private String carrierDriverName;
        //司机电话
        private String carrierDriverPhone;
        //消息发送时间
        private Date createTime;
        //始发站
        private String startStationCode;
        //始发站
        private String startStationName;
        //目的站
        private String destinationStationCode;
        //目的站
        private String destinationStationCodeName;

        public String getTransBookCode() {
            return transBookCode;
        }

        public void setTransBookCode(String transBookCode) {
            this.transBookCode = transBookCode;
        }

        public String getTransJobCode() {
            return transJobCode;
        }

        public void setTransJobCode(String transJobCode) {
            this.transJobCode = transJobCode;
        }

        public String getTransJobItemCode() {
            return transJobItemCode;
        }

        public void setTransJobItemCode(String transJobItemCode) {
            this.transJobItemCode = transJobItemCode;
        }

        public String getTransWorkCode() {
            return transWorkCode;
        }

        public void setTransWorkCode(String transWorkCode) {
            this.transWorkCode = transWorkCode;
        }

        public String getTransWorkItemCode() {
            return transWorkItemCode;
        }

        public void setTransWorkItemCode(String transWorkItemCode) {
            this.transWorkItemCode = transWorkItemCode;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public Integer getBusinessType() {
            return businessType;
        }

        public void setBusinessType(Integer businessType) {
            this.businessType = businessType;
        }

        public String getSealCarCode() {
            return sealCarCode;
        }

        public void setSealCarCode(String sealCarCode) {
            this.sealCarCode = sealCarCode;
        }

        public String getBillCode() {
            return billCode;
        }

        public void setBillCode(String billCode) {
            this.billCode = billCode;
        }

        public Integer getBillType() {
            return billType;
        }

        public void setBillType(Integer billType) {
            this.billType = billType;
        }

        public Integer getSendCarInArea() {
            return sendCarInArea;
        }

        public void setSendCarInArea(Integer sendCarInArea) {
            this.sendCarInArea = sendCarInArea;
        }

        public Date getSendCarTime() {
            return sendCarTime;
        }

        public void setSendCarTime(Date sendCarTime) {
            this.sendCarTime = sendCarTime;
        }

        public Date getArriveCarTime() {
            return arriveCarTime;
        }

        public void setArriveCarTime(Date arriveCarTime) {
            this.arriveCarTime = arriveCarTime;
        }

        public Integer getArriveCarInArea() {
            return arriveCarInArea;
        }

        public void setArriveCarInArea(Integer arriveCarInArea) {
            this.arriveCarInArea = arriveCarInArea;
        }

        public String getWaybillCode() {
            return waybillCode;
        }

        public void setWaybillCode(String waybillCode) {
            this.waybillCode = waybillCode;
        }

        public String getPackageCode() {
            return packageCode;
        }

        public void setPackageCode(String packageCode) {
            this.packageCode = packageCode;
        }

        public String getCarrierCode() {
            return carrierCode;
        }

        public void setCarrierCode(String carrierCode) {
            this.carrierCode = carrierCode;
        }

        public String getCarrierName() {
            return carrierName;
        }

        public void setCarrierName(String carrierName) {
            this.carrierName = carrierName;
        }

        public Date getPlanDepartTime() {
            return planDepartTime;
        }

        public void setPlanDepartTime(Date planDepartTime) {
            this.planDepartTime = planDepartTime;
        }

        public Date getPlanArriveTime() {
            return planArriveTime;
        }

        public void setPlanArriveTime(Date planArriveTime) {
            this.planArriveTime = planArriveTime;
        }

        public String getBeginNodeCode() {
            return beginNodeCode;
        }

        public void setBeginNodeCode(String beginNodeCode) {
            this.beginNodeCode = beginNodeCode;
        }

        public String getBeginNodeName() {
            return beginNodeName;
        }

        public void setBeginNodeName(String beginNodeName) {
            this.beginNodeName = beginNodeName;
        }

        public String getEndNodeCode() {
            return endNodeCode;
        }

        public void setEndNodeCode(String endNodeCode) {
            this.endNodeCode = endNodeCode;
        }

        public String getEndNodeName() {
            return endNodeName;
        }

        public void setEndNodeName(String endNodeName) {
            this.endNodeName = endNodeName;
        }

        public Integer getSendArriveType() {
            return sendArriveType;
        }

        public void setSendArriveType(Integer sendArriveType) {
            this.sendArriveType = sendArriveType;
        }

        public String getOperateUserCode() {
            return operateUserCode;
        }

        public void setOperateUserCode(String operateUserCode) {
            this.operateUserCode = operateUserCode;
        }

        public String getOperateUserName() {
            return operateUserName;
        }

        public void setOperateUserName(String operateUserName) {
            this.operateUserName = operateUserName;
        }

        public Date getOperateTime() {
            return operateTime;
        }

        public void setOperateTime(Date operateTime) {
            this.operateTime = operateTime;
        }

        public String getTransportCode() {
            return transportCode;
        }

        public void setTransportCode(String transportCode) {
            this.transportCode = transportCode;
        }

        public String getRouteLineCode() {
            return routeLineCode;
        }

        public void setRouteLineCode(String routeLineCode) {
            this.routeLineCode = routeLineCode;
        }

        public Integer getSendArriveMatchFlag() {
            return sendArriveMatchFlag;
        }

        public void setSendArriveMatchFlag(Integer sendArriveMatchFlag) {
            this.sendArriveMatchFlag = sendArriveMatchFlag;
        }

        public Date getAreaSendCarTime() {
            return areaSendCarTime;
        }

        public void setAreaSendCarTime(Date areaSendCarTime) {
            this.areaSendCarTime = areaSendCarTime;
        }

        public Date getAreaArriveCarTime() {
            return areaArriveCarTime;
        }

        public void setAreaArriveCarTime(Date areaArriveCarTime) {
            this.areaArriveCarTime = areaArriveCarTime;
        }

        public Integer getAreaSendArriveMatchFlag() {
            return areaSendArriveMatchFlag;
        }

        public void setAreaSendArriveMatchFlag(Integer areaSendArriveMatchFlag) {
            this.areaSendArriveMatchFlag = areaSendArriveMatchFlag;
        }

        public String getCarrierDriverCode() {
            return carrierDriverCode;
        }

        public void setCarrierDriverCode(String carrierDriverCode) {
            this.carrierDriverCode = carrierDriverCode;
        }

        public String getCarrierDriverName() {
            return carrierDriverName;
        }

        public void setCarrierDriverName(String carrierDriverName) {
            this.carrierDriverName = carrierDriverName;
        }

        public String getCarrierDriverPhone() {
            return carrierDriverPhone;
        }

        public void setCarrierDriverPhone(String carrierDriverPhone) {
            this.carrierDriverPhone = carrierDriverPhone;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public String getStartStationCode() {
            return startStationCode;
        }

        public void setStartStationCode(String startStationCode) {
            this.startStationCode = startStationCode;
        }

        public String getStartStationName() {
            return startStationName;
        }

        public void setStartStationName(String startStationName) {
            this.startStationName = startStationName;
        }

        public String getDestinationStationCode() {
            return destinationStationCode;
        }

        public void setDestinationStationCode(String destinationStationCode) {
            this.destinationStationCode = destinationStationCode;
        }

        public String getDestinationStationCodeName() {
            return destinationStationCodeName;
        }

        public void setDestinationStationCodeName(String destinationStationCodeName) {
            this.destinationStationCodeName = destinationStationCodeName;
        }
    }
}
