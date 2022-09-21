package com.jd.bluedragon.distribution.label.domain.farmar;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 砝码实体
 *
 * @author hujiping
 * @date 2022/8/22 5:08 PM
 */
public class FarmarEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 砝码编码
     */
    private String farmarCode;
    /**
     * 区域ID
     */
    private Integer orgId;
    /**
     * 区域名称
     */
    private String orgName;
    /**
     * 场地ID
     */
    private Integer createSiteCode;
    /**
     * 场地名称
     */
    private String createSiteName;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 砝码重量
     */
    private BigDecimal weight;
    /**
     * 砝码长
     */
    private BigDecimal length;
    /**
     * 砝码宽
     */
    private BigDecimal width;
    /**
     * 砝码高
     */
    private BigDecimal high;
    /**
     * 砝码校验类型
     * @see com.jd.bluedragon.distribution.label.enums.FarmarCheckTypeEnum
     *  0、重量标准 1、尺寸标准
     */
    private Integer farmarCheckType;

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

    public String getFarmarCode() {
        return farmarCode;
    }

    public void setFarmarCode(String farmarCode) {
        this.farmarCode = farmarCode;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public Integer getFarmarCheckType() {
        return farmarCheckType;
    }

    public void setFarmarCheckType(Integer farmarCheckType) {
        this.farmarCheckType = farmarCheckType;
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
