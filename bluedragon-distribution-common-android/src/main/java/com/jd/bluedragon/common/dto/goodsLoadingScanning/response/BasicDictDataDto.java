package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2021-03-08 16:41
 */
public class BasicDictDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public BasicDictDataDto() {
    }

    public Long getDictId() {
        return this.dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return this.dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictCode() {
        return this.dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getParentCode() {
        return this.parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getDictLevel() {
        return this.dictLevel;
    }

    public void setDictLevel(Integer dictLevel) {
        this.dictLevel = dictLevel;
    }

    public String getDictGroup() {
        return this.dictGroup;
    }

    public void setDictGroup(String dictGroup) {
        this.dictGroup = dictGroup;
    }

    public Integer getDictSequence() {
        return this.dictSequence;
    }

    public void setDictSequence(Integer dictSequence) {
        this.dictSequence = dictSequence;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getSysTime() {
        return this.sysTime;
    }

    public void setSysTime(Date sysTime) {
        this.sysTime = sysTime;
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
