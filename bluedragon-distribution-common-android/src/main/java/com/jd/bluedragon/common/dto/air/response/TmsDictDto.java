package com.jd.bluedragon.common.dto.air.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/11/4
 */
public class TmsDictDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long dictId;

    private int yn;

    private Long createTime;

    private Long sysTime;

    private Long updateTime;

    private String owner;

    private String remark;

    private int dictSequence;

    private String dictGroup;

    private int dictLevel;

    private String parentCode;

    private String dictCode;

    private String dictName;

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public int getYn() {
        return yn;
    }

    public void setYn(int yn) {
        this.yn = yn;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getDictSequence() {
        return dictSequence;
    }

    public void setDictSequence(int dictSequence) {
        this.dictSequence = dictSequence;
    }

    public String getDictGroup() {
        return dictGroup;
    }

    public void setDictGroup(String dictGroup) {
        this.dictGroup = dictGroup;
    }

    public int getDictLevel() {
        return dictLevel;
    }

    public void setDictLevel(int dictLevel) {
        this.dictLevel = dictLevel;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}
