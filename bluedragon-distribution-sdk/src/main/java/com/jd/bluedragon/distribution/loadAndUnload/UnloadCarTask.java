package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @author lijie
 * @date 2020/6/24 14:14
 */
public class UnloadCarTask extends DbEntity {

    /**
     * 卸车任务主键ID
     * */
    private Long unloadCarId;
    /**
     * 车牌号
     * */
    private String carCode;
    /**
     * 封车编码
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
     * 协助人ERP
     * */
    private String helperErps;
    /**
     * 协助人名称
     * */
    private String helperNames;
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
     * 卸车任务状态
     * @see UnloadCarStatusEnum
     * */
    private Integer status;
    /**
     * 是否有效
     * */
    private Integer yn;

    public Long getUnloadCarId() {
        return unloadCarId;
    }

    public void setUnloadCarId(Long unloadCarId) {
        this.unloadCarId = unloadCarId;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
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

    public String getHelperErps() {
        return helperErps;
    }

    public void setHelperErps(String helperErps) {
        this.helperErps = helperErps;
    }

    public String getHelperNames() {
        return helperNames;
    }

    public void setHelperNames(String helperNames) {
        this.helperNames = helperNames;
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
}
