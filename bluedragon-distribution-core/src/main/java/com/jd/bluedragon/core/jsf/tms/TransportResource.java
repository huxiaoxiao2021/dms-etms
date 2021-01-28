package com.jd.bluedragon.core.jsf.tms;

import java.io.Serializable;
import java.util.Date;

/**
 * 运力资源包装
 * Created by wangtingwei on 2017/5/18.
 */
public class TransportResource  implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;

    private Integer transCodeType;

    private String transCodeTypeName; //运力编码类型名称

    private String transCode;

    private Integer startOrgId;

    private String startOrgName;

    private Integer startProvinceId;

    private String startProvinceName;

    private Integer startCityId;

    private String startCityName;

    private Integer startNodeType;

    private String startNodeTypeName;

    private Integer startNodeId;

    private String startNodeCode;

    private String startNodeName;

    private Integer endOrgId;

    private String endOrgName;

    private Integer endProvinceId;

    private String endProvinceName;

    private Integer endCityId;

    private String endCityName;

    private Integer endNodeType;

    private String endNodeTypeName;

    private Integer endNodeId;

    private String endNodeCode;

    private String endNodeName;

    private Integer resourceType;

    private Integer routeType;

    private String routeTypeName;

    private Integer transType;

    private String transTypeName;

    private Integer transMode;

    private String transModeName;

    private Integer carrierId;

    private String carrierCode;

    private String carrierName;

    private Integer carrierOrgId;

    private String carrierOrgName;

    private String routeLineCode;

    private Double distance;

    private Long sendCarTime;

    private String sendCarTimeStr;

    private int sendCarDay;

    private int sendCarHour;

    private int sendCarMin;

    private Long arriveCarTime;

    private String arriveCarTimeStr;

    private int arriveCarDay;

    private int arriveCarHour;

    private int arriveCarMin;

    private Long travelTime;

    private Double travelTimeH;

    private String createOperatorErp;

    private String createOperatorName;

    private Date createTime;

    private String updateOperatorErp;

    private String updateOperatorName;

    private Date updateTime;

    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTransCodeType() {
        return transCodeType;
    }

    public void setTransCodeType(Integer transCodeType) {
        this.transCodeType = transCodeType;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public Integer getStartOrgId() {
        return startOrgId;
    }

    public void setStartOrgId(Integer startOrgId) {
        this.startOrgId = startOrgId;
    }

    public String getStartOrgName() {
        return startOrgName;
    }

    public void setStartOrgName(String startOrgName) {
        this.startOrgName = startOrgName;
    }

    public Integer getStartProvinceId() {
        return startProvinceId;
    }

    public void setStartProvinceId(Integer startProvinceId) {
        this.startProvinceId = startProvinceId;
    }

    public String getStartProvinceName() {
        return startProvinceName;
    }

    public void setStartProvinceName(String startProvinceName) {
        this.startProvinceName = startProvinceName;
    }

    public Integer getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Integer startCityId) {
        this.startCityId = startCityId;
    }

    public String getStartCityName() {
        return startCityName;
    }

    public void setStartCityName(String startCityName) {
        this.startCityName = startCityName;
    }

    public Integer getStartNodeType() {
        return startNodeType;
    }

    public void setStartNodeType(Integer startNodeType) {
        this.startNodeType = startNodeType;
    }

    public String getStartNodeTypeName() {
        return startNodeTypeName;
    }

    public void setStartNodeTypeName(String startNodeTypeName) {
        this.startNodeTypeName = startNodeTypeName;
    }

    public Integer getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(Integer startNodeId) {
        this.startNodeId = startNodeId;
    }

    public String getStartNodeCode() {
        return startNodeCode;
    }

    public void setStartNodeCode(String startNodeCode) {
        this.startNodeCode = startNodeCode;
    }

    public String getStartNodeName() {
        return startNodeName;
    }

    public void setStartNodeName(String startNodeName) {
        this.startNodeName = startNodeName;
    }

    public Integer getEndOrgId() {
        return endOrgId;
    }

    public void setEndOrgId(Integer endOrgId) {
        this.endOrgId = endOrgId;
    }

    public String getEndOrgName() {
        return endOrgName;
    }

    public void setEndOrgName(String endOrgName) {
        this.endOrgName = endOrgName;
    }

    public Integer getEndProvinceId() {
        return endProvinceId;
    }

    public void setEndProvinceId(Integer endProvinceId) {
        this.endProvinceId = endProvinceId;
    }

    public String getEndProvinceName() {
        return endProvinceName;
    }

    public void setEndProvinceName(String endProvinceName) {
        this.endProvinceName = endProvinceName;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public Integer getEndNodeType() {
        return endNodeType;
    }

    public void setEndNodeType(Integer endNodeType) {
        this.endNodeType = endNodeType;
    }

    public String getEndNodeTypeName() {
        return endNodeTypeName;
    }

    public void setEndNodeTypeName(String endNodeTypeName) {
        this.endNodeTypeName = endNodeTypeName;
    }

    public Integer getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Integer endNodeId) {
        this.endNodeId = endNodeId;
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

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public String getRouteTypeName() {
        return routeTypeName;
    }

    public void setRouteTypeName(String routeTypeName) {
        this.routeTypeName = routeTypeName;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public String getTransTypeName() {
        return transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }

    public Integer getTransMode() {
        return transMode;
    }

    public void setTransMode(Integer transMode) {
        this.transMode = transMode;
    }

    public String getTransModeName() {
        return transModeName;
    }

    public void setTransModeName(String transModeName) {
        this.transModeName = transModeName;
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

    public Integer getCarrierOrgId() {
        return carrierOrgId;
    }

    public void setCarrierOrgId(Integer carrierOrgId) {
        this.carrierOrgId = carrierOrgId;
    }

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getSendCarTime() {
        return sendCarTime;
    }

    public void setSendCarTime(Long sendCarTime) {
        this.sendCarTime = sendCarTime;
    }

    public int getSendCarDay() {
        return sendCarDay;
    }

    public void setSendCarDay(int sendCarDay) {
        this.sendCarDay = sendCarDay;
    }

    public int getSendCarHour() {
        return sendCarHour;
    }

    public void setSendCarHour(int sendCarHour) {
        this.sendCarHour = sendCarHour;
    }

    public int getSendCarMin() {
        return sendCarMin;
    }

    public void setSendCarMin(int sendCarMin) {
        this.sendCarMin = sendCarMin;
    }

    public Long getArriveCarTime() {
        return arriveCarTime;
    }

    public void setArriveCarTime(Long arriveCarTime) {
        this.arriveCarTime = arriveCarTime;
    }

    public int getArriveCarDay() {
        return arriveCarDay;
    }

    public void setArriveCarDay(int arriveCarDay) {
        this.arriveCarDay = arriveCarDay;
    }

    public int getArriveCarHour() {
        return arriveCarHour;
    }

    public void setArriveCarHour(int arriveCarHour) {
        this.arriveCarHour = arriveCarHour;
    }

    public int getArriveCarMin() {
        return arriveCarMin;
    }

    public void setArriveCarMin(int arriveCarMin) {
        this.arriveCarMin = arriveCarMin;
    }

    public Long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Long travelTime) {
        this.travelTime = travelTime;
    }

    public Double getTravelTimeH() {
        return travelTimeH;
    }

    public void setTravelTimeH(Double travelTimeH) {
        this.travelTimeH = travelTimeH;
    }

    public String getCreateOperatorErp() {
        return createOperatorErp;
    }

    public void setCreateOperatorErp(String createOperatorErp) {
        this.createOperatorErp = createOperatorErp;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public void setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateOperatorErp() {
        return updateOperatorErp;
    }

    public void setUpdateOperatorErp(String updateOperatorErp) {
        this.updateOperatorErp = updateOperatorErp;
    }

    public String getUpdateOperatorName() {
        return updateOperatorName;
    }

    public void setUpdateOperatorName(String updateOperatorName) {
        this.updateOperatorName = updateOperatorName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getCarrierOrgName() {
        return carrierOrgName;
    }

    public void setCarrierOrgName(String carrierOrgName) {
        this.carrierOrgName = carrierOrgName;
    }

    public String getTransCodeTypeName() {
        return transCodeTypeName;
    }

    public void setTransCodeTypeName(String transCodeTypeName) {
        this.transCodeTypeName = transCodeTypeName;
    }

    public String getSendCarTimeStr() {
        return sendCarTimeStr;
    }

    public void setSendCarTimeStr(String sendCarTimeStr) {
        this.sendCarTimeStr = sendCarTimeStr;
    }

    public String getArriveCarTimeStr() {
        return arriveCarTimeStr;
    }

    public void setArriveCarTimeStr(String arriveCarTimeStr) {
        this.arriveCarTimeStr = arriveCarTimeStr;
    }

    public Integer getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Integer carrierId) {
        this.carrierId = carrierId;
    }
}
