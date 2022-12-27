package com.jd.bluedragon.distribution.jy.dto.task;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName UnloadVehicleMqDto
 * @Description
 * @Author wyh
 * @Date 2022/4/7 16:39
 **/
public class UnloadVehicleMqDto implements Serializable {

    private static final long serialVersionUID = -2720516986077944201L;

    public static final String EXTEND_KEY_LOST_CNT = "lostCnt";
    public static final String EXTEND_KEY_DAMAGE_CNT = "damageCnt";
    public static final String EXTEND_KEY_SCAN_PROGRESS = "totalScannedPackageProgress";
    public static final String EXTEND_KEY_TOTAL_COUNT = "totalSealPackageCount";

    public static final String EXTEND_KEY_MORESCAN_COUNT = "moreScanTotalCount";
    public static final String EXTEND_KEY_BOARD_COUNT = "totalScannedBoardCount";
    public static final String EXTEND_KEY_INTERCEPT_COUNT = "totalScannedInterceptCount";
    // 封车编码

    private String sealCarCode;

    // 任务编号

    private String billCode;

    // 车牌号

    private String vehicleNumber;

    // 车牌号后四位

    private String vehicleNumberLastFour;

    // 运力编码

    private String transportCode;

    // 批次号列表，逗号分隔

    private String sendCodeList;

    // 封签号列表，逗号分隔

    private String sealCodeList;

    // 封车时间

    private Date sealCarTime;

    // 始发站点编号(七位编码)

    private String startSiteCode;

    // 始发站点ID

    private Integer startSiteId;

    // 始发站点名称

    private String startSiteName;

    // 始发机构编号

    private Integer startOrgCode;

    // 始发机构名称

    private String startOrgName;

    // 目的站点编号(七位编码)

    private String endSiteCode;

    // 目的站点ID

    private Integer endSiteId;

    // 目的站点名称

    private String endSiteName;

    // 目的机构编号

    private Integer endOrgCode;

    // 目的机构名称

    private String endOrgName;

    // 解封车时间

    private Date desealCarTime;

    // 检查类型，枚举值检查类型，1-抽检

    private Integer checkType;

    // 线路类型

    private Integer lineSourceType;

    // 线路类型

    private String lineSourceTypeName;

    // 线路类型

    private Integer lineType;

    // 线路类型描述

    private String lineTypeName;

    // 车型

    private String carModel;

    // 委托书号

    private String transBookCode;

    // 预计到达时间

    private Date predictionArriveTime;

    // 实际到达时间

    private Date actualArriveTime;

    // 用于排序的时间，优先取「实际到达时间」，若不存在再取「预计到达时间」

    private Date orderTime;

    // 总件数

    private Long totalCount;

    // 车辆状态, 1-待解，2-待卸，3-卸车，4-在途
    private Integer vehicleStatus;

    // 积分，根据明细条件、类型等计算出
    private Double fraction;

    // 排名

    private Integer ranking;

    // 是否有效，1-有效，0-无效

    private Integer yn;

    // 封车状态，10-封车，20-解封, 200-取消封车
    private Integer sealCarStatus;

    // 实际发出时间
    private Date actualSendTime;

    // 车辆卸货状态，0-未卸 1-已卸（只要有一单验货，则视为已卸）
    private Integer unloadState;

    // 已卸包裹总数-用于计算总进度
    private Integer totalScannedPackageCount;

    /**
     * 扩展字段
     * lostCnt 丢失
     * damageCnt 破损
     * totalScannedPackageProgress 进度（0.00格式）
     */
    private Map<String, Object> extendInfo;

    /**
     * 数据版本号
     */
    private Integer version;

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

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumberLastFour() {
        return vehicleNumberLastFour;
    }

    public void setVehicleNumberLastFour(String vehicleNumberLastFour) {
        this.vehicleNumberLastFour = vehicleNumberLastFour;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(String sendCodeList) {
        this.sendCodeList = sendCodeList;
    }

    public String getSealCodeList() {
        return sealCodeList;
    }

    public void setSealCodeList(String sealCodeList) {
        this.sealCodeList = sealCodeList;
    }

    public Date getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(Date sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Integer getStartOrgCode() {
        return startOrgCode;
    }

    public void setStartOrgCode(Integer startOrgCode) {
        this.startOrgCode = startOrgCode;
    }

    public String getStartOrgName() {
        return startOrgName;
    }

    public void setStartOrgName(String startOrgName) {
        this.startOrgName = startOrgName;
    }

    public String getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(String endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Integer getEndOrgCode() {
        return endOrgCode;
    }

    public void setEndOrgCode(Integer endOrgCode) {
        this.endOrgCode = endOrgCode;
    }

    public String getEndOrgName() {
        return endOrgName;
    }

    public void setEndOrgName(String endOrgName) {
        this.endOrgName = endOrgName;
    }

    public Date getDesealCarTime() {
        return desealCarTime;
    }

    public void setDesealCarTime(Date desealCarTime) {
        this.desealCarTime = desealCarTime;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public Integer getLineSourceType() {
        return lineSourceType;
    }

    public void setLineSourceType(Integer lineSourceType) {
        this.lineSourceType = lineSourceType;
    }

    public String getLineSourceTypeName() {
        return lineSourceTypeName;
    }

    public void setLineSourceTypeName(String lineSourceTypeName) {
        this.lineSourceTypeName = lineSourceTypeName;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getTransBookCode() {
        return transBookCode;
    }

    public void setTransBookCode(String transBookCode) {
        this.transBookCode = transBookCode;
    }

    public Date getPredictionArriveTime() {
        return predictionArriveTime;
    }

    public void setPredictionArriveTime(Date predictionArriveTime) {
        this.predictionArriveTime = predictionArriveTime;
    }

    public Date getActualArriveTime() {
        return actualArriveTime;
    }

    public void setActualArriveTime(Date actualArriveTime) {
        this.actualArriveTime = actualArriveTime;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Double getFraction() {
        return fraction;
    }

    public void setFraction(Double fraction) {
        this.fraction = fraction;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getSealCarStatus() {
        return sealCarStatus;
    }

    public void setSealCarStatus(Integer sealCarStatus) {
        this.sealCarStatus = sealCarStatus;
    }

    public Date getActualSendTime() {
        return actualSendTime;
    }

    public void setActualSendTime(Date actualSendTime) {
        this.actualSendTime = actualSendTime;
    }

    public Integer getUnloadState() {
        return unloadState;
    }

    public void setUnloadState(Integer unloadState) {
        this.unloadState = unloadState;
    }

    public Integer getTotalScannedPackageCount() {
        return totalScannedPackageCount;
    }

    public void setTotalScannedPackageCount(Integer totalScannedPackageCount) {
        this.totalScannedPackageCount = totalScannedPackageCount;
    }

    public Map<String, Object> getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(Map<String, Object> extendInfo) {
        this.extendInfo = extendInfo;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
