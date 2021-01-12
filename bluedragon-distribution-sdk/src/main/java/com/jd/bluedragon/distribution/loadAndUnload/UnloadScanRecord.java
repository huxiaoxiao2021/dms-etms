package com.jd.bluedragon.distribution.loadAndUnload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 卸车扫描运单暂存表
 * @author lvyuan21
 * @date 2020-12-25 16:19
 */
public class UnloadScanRecord implements Serializable {

    private static final long serialVersionUID = -7623509285189482980L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String licenseNumber;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 创建人所属转运中心Id
     */
    private Integer createSiteCode;

    /**
     * 创建人所属转运中心名称
     */
    private String createSiteName;

    /**
     * 目的场地Id
     */
    private Integer endSiteCode;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 是否将包裹号转大宗：2是，0否
     */
    private Integer transfer;

    /**
     * 多扫状态标记
     */
    private Integer flowDisaccord;

    /**
     * 创建人账号
     */
    private Integer createUserCode;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 更新人账号
     */
    private Integer updateUserCode;

    /**
     * 更新人名称
     */
    private String updateUserName;

    /**
     * 更新人Erp
     */
    private String updateUserErp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 可用状态: 1 可用, 0 不可用
     */
    private Integer yn;

    /**
     * 数据库时间
     */
    private Date ts;

    /**
     * 包裹号集合
     */
    private List<String> packageCodeList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
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

    public Integer getTransfer() {
        return transfer;
    }

    public void setTransfer(Integer transfer) {
        this.transfer = transfer;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Integer getFlowDisaccord() {
        return flowDisaccord;
    }

    public void setFlowDisaccord(Integer flowDisaccord) {
        this.flowDisaccord = flowDisaccord;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public List<String> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<String> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }
}
