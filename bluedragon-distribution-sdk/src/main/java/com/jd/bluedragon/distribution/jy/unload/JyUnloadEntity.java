package com.jd.bluedragon.distribution.jy.unload;


import java.io.Serializable;
import java.util.Date;

/**
 * 卸车任务明细表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:45:20
 */
public class JyUnloadEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public JyUnloadEntity() {}

    public JyUnloadEntity(String barCode, Long operateSiteId, String bizId) {
        this.barCode = barCode;
        this.operateSiteId = operateSiteId;
        this.bizId = bizId;
    }

    public JyUnloadEntity(Long operateSiteId, String bizId) {
        this.operateSiteId = operateSiteId;
        this.bizId = bizId;
    }

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 始发场地ID
     */
    private Long startSiteId;
    /**
     * 目的场地ID
     */
    private Long endSiteId;

    /**
     * 是否无任务卸车；1：是：0：否
     */
    private Integer manualCreatedFlag;

    /**
     * 操作场地ID
     */
    private Long operateSiteId;
    /**
     * 单号
     */
    private String barCode;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 更新人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setBizId(String bizId) {
        return this.bizId = bizId;
    }

    public String getBizId() {
        return this.bizId;
    }

    public String setSealCarCode(String sealCarCode) {
        return this.sealCarCode = sealCarCode;
    }

    public String getSealCarCode() {
        return this.sealCarCode;
    }

    public String setVehicleNumber(String vehicleNumber) {
        return this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleNumber() {
        return this.vehicleNumber;
    }

    public Long setStartSiteId(Long startSiteId) {
        return this.startSiteId = startSiteId;
    }

    public Long getStartSiteId() {
        return this.startSiteId;
    }

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public Long setEndSiteId(Long endSiteId) {
        return this.endSiteId = endSiteId;
    }

    public Long getEndSiteId() {
        return this.endSiteId;
    }

    public Long setOperateSiteId(Long operateSiteId) {
        return this.operateSiteId = operateSiteId;
    }

    public Long getOperateSiteId() {
        return this.operateSiteId;
    }

    public String setBarCode(String barCode) {
        return this.barCode = barCode;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public Date setOperateTime(Date operateTime) {
        return this.operateTime = operateTime;
    }

    public Date getOperateTime() {
        return this.operateTime;
    }

    public String setCreateUserErp(String createUserErp) {
        return this.createUserErp = createUserErp;
    }

    public String getCreateUserErp() {
        return this.createUserErp;
    }

    public String setCreateUserName(String createUserName) {
        return this.createUserName = createUserName;
    }

    public String getCreateUserName() {
        return this.createUserName;
    }

    public String setUpdateUserErp(String updateUserErp) {
        return this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserErp() {
        return this.updateUserErp;
    }

    public String setUpdateUserName(String updateUserName) {
        return this.updateUserName = updateUserName;
    }

    public String getUpdateUserName() {
        return this.updateUserName;
    }

    public Date setCreateTime(Date createTime) {
        return this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Date setUpdateTime(Date updateTime) {
        return this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public Integer setYn(Integer yn) {
        return this.yn = yn;
    }

    public Integer getYn() {
        return this.yn;
    }

    public Date setTs(Date ts) {
        return this.ts = ts;
    }

    public Date getTs() {
        return this.ts;
    }

}
