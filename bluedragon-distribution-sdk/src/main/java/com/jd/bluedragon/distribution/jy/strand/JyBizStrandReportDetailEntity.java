package com.jd.bluedragon.distribution.jy.strand;

import java.io.Serializable;
import java.util.Date;

/**
 * 拣运-滞留上报明细数据库实体
 *
 * @author hujiping
 * @date 2023/3/28 3:49 PM
 */
public class JyBizStrandReportDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 扫描条码
     */
    private String scanBarCode;

    /**
     * 扫描条码关联的容器（包裹/运单/箱/板）
     */
    private String containerCode;

    /**
     * 容器内扫描件数量
     */
    private Integer containerInnerCount;

    /**
     * 扫货方式
     */
    private Integer scanType;

    /**
     * 是否取消
     */
    private Integer isCancel;

    /**
     * 工序主键
     */
    private String refGridKey;
    
    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 创建人用户ID
     */
    private Integer createUserId;

    /**
     * 创建人ERP
     */
    private String createUserErp;

    /**
     * 更新人ERP
     */
    private String updateUserErp;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 逻辑删除标志,0-删除,1-正常
     */
    private Integer yn;
    
    /**
     * 数据库时间
     */
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getScanBarCode() {
        return scanBarCode;
    }

    public void setScanBarCode(String scanBarCode) {
        this.scanBarCode = scanBarCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public Integer getContainerInnerCount() {
        return containerInnerCount;
    }

    public void setContainerInnerCount(Integer containerInnerCount) {
        this.containerInnerCount = containerInnerCount;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public String getRefGridKey() {
        return refGridKey;
    }

    public void setRefGridKey(String refGridKey) {
        this.refGridKey = refGridKey;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
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

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
