package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * 卸车任务表
 *
 * @author: hujiping
 * @date: 2020/6/23 10:54
 */
public class UnloadCar extends DbEntity {

    /**
     * 卸车任务主键ID
     * */
    private Long unloadCarId;
    /**
     * 车牌号
     * */
    private String vehicleNumber;
    /**
     * 封车编码（任务编码）
     * */
    private String sealCarCode;
    /**
     * 上游机构ID
     * */
    private Integer startSiteCode;
    /**
     * 上游机构名称
     * */
    private String startSiteName;
    /**
     * 下游机构ID
     * */
    private Integer endSiteCode;
    /**
     * 下游机构名称
     * */
    private String endSiteName;
    /**
     * 封车时间
     * */
    private Date sealTime;
    /**
     * 封车号
     * */
    private String sealCode;
    /**
     * 批次号
     * */
    private String batchCode;
    /**
     * 月台号
     * */
    private String railWayPlatForm;
    /**
     * 运单数量
     * */
    private Integer waybillNum;
    /**
     * 包裹数量
     * */
    private Integer packageNum;
    /**
     * 卸车人ERP
     * */
    private String unloadUserErp;
    /**
     * 卸车人名称
     * */
    private String unloadUserName;
    /**
     * 分配时间
     * */
    private Date distributeTime;
    /**
     * 更新人ERP
     * */
    private String updateUserErp;
    /**
     * 更新人名称
     * */
    private String updateUserName;
    /**
     * 操作时间
     * */
    private Date operateTime;
    /**
     * 操作人ERP
     * */
    private String operateUserErp;
    /**
     * 操作人姓名
     * */
    private String operateUserName;
    /**
     * 卸车任务状态
     * @see com.jd.bluedragon.common.dto.unloadCar.UnloadCarStatusEnum
     * */
    private Integer status;
    /**
     * 是否有效
     * */
    private Integer yn;

    /**
     * 卸车模式: 1-人工, 0-流水线
     */
    private Integer type;

    /**
     * 任务开始时间 ： 首次扫描时间
     */
    private Date startTime;

    private Date endTime;
    /**
     * 运输方式
     */
    private Integer transWay;
    /**
     * 卸车时效（单位：分钟）
     */
    private Integer duration;
    /**
     * 卸车时效类型 ：0-默认方式（120min）1-调用路由时间
     */
    private Integer durationType;
    /**
     * 任务完成状态：0-正常完成，1-超时完成
     */
    private Integer endStatus;
    /**
     * AppVersionEnums
     */
    private String version;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getUnloadCarId() {
        return unloadCarId;
    }

    public void setUnloadCarId(Long unloadCarId) {
        this.unloadCarId = unloadCarId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(Integer startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Integer getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(Integer endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Date getSealTime() {
        return sealTime;
    }

    public void setSealTime(Date sealTime) {
        this.sealTime = sealTime;
    }

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getRailWayPlatForm() {
        return railWayPlatForm;
    }

    public void setRailWayPlatForm(String railWayPlatForm) {
        this.railWayPlatForm = railWayPlatForm;
    }

    public Integer getWaybillNum() {
        return waybillNum;
    }

    public void setWaybillNum(Integer waybillNum) {
        this.waybillNum = waybillNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public String getUnloadUserErp() {
        return unloadUserErp;
    }

    public void setUnloadUserErp(String unloadUserErp) {
        this.unloadUserErp = unloadUserErp;
    }

    public String getUnloadUserName() {
        return unloadUserName;
    }

    public void setUnloadUserName(String unloadUserName) {
        this.unloadUserName = unloadUserName;
    }

    public Date getDistributeTime() {
        return distributeTime;
    }

    public void setDistributeTime(Date distributeTime) {
        this.distributeTime = distributeTime;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDurationType() {
        return durationType;
    }

    public void setDurationType(Integer durationType) {
        this.durationType = durationType;
    }

    public Integer getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(Integer endStatus) {
        this.endStatus = endStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
