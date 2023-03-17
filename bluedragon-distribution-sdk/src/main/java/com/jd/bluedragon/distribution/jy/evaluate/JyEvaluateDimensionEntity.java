package com.jd.bluedragon.distribution.jy.evaluate;

import java.io.Serializable;
import java.util.Date;

public class JyEvaluateDimensionEntity implements Serializable {

    private static final long serialVersionUID = 8821734157832978341L;

    private Long id;

    /**
     * 评价维度编码
     */
    private Integer dimensionCode;

    /**
     * 评价维度名称
     */
    private String dimensionName;

    /**
     * 评价维度状态
     */
    private Integer dimensionStatus;

    /**
     * 是否有文本框：0-无，1-有
     */
    private Integer hasTextBox;

    /**
     * 备注
     */
    private String remark;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(Integer dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public Integer getDimensionStatus() {
        return dimensionStatus;
    }

    public void setDimensionStatus(Integer dimensionStatus) {
        this.dimensionStatus = dimensionStatus;
    }

    public Integer getHasTextBox() {
        return hasTextBox;
    }

    public void setHasTextBox(Integer hasTextBox) {
        this.hasTextBox = hasTextBox;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}