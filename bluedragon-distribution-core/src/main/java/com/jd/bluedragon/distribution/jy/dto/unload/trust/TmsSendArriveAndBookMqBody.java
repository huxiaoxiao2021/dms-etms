package com.jd.bluedragon.distribution.jy.dto.unload.trust;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2024/3/4 14:54
 * @Description
 *
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
public class TmsSendArriveAndBookMqBody implements Serializable {
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
