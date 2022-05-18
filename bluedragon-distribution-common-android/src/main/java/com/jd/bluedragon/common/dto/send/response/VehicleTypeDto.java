package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.Date;

public class VehicleTypeDto implements Serializable {

    private static final long serialVersionUID = -3930599933845221946L;

    private Long dictId;
    private String dictName;
    private String dictCode;
    private String parentCode;
    private Integer dictLevel;
    private String dictGroup;
    private Integer dictSequence;
    private String remark;
    private String owner;
    private Date createTime;
    private Date updateTime;
    private Date sysTime;
    private Integer yn;

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getDictLevel() {
        return dictLevel;
    }

    public void setDictLevel(Integer dictLevel) {
        this.dictLevel = dictLevel;
    }

    public String getDictGroup() {
        return dictGroup;
    }

    public void setDictGroup(String dictGroup) {
        this.dictGroup = dictGroup;
    }

    public Integer getDictSequence() {
        return dictSequence;
    }

    public void setDictSequence(Integer dictSequence) {
        this.dictSequence = dictSequence;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public Date getSysTime() {
        return sysTime;
    }

    public void setSysTime(Date sysTime) {
        this.sysTime = sysTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    @Override
    public String toString() {
        return "VehicleTypeDto{" +
                "dictId=" + dictId +
                ", dictName='" + dictName + '\'' +
                ", dictCode='" + dictCode + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", dictLevel=" + dictLevel +
                ", dictGroup='" + dictGroup + '\'' +
                ", dictSequence=" + dictSequence +
                ", remark='" + remark + '\'' +
                ", owner='" + owner + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", sysTime=" + sysTime +
                ", yn=" + yn +
                '}';
    }
}
