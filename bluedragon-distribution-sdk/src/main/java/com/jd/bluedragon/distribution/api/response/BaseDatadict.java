package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dudong on 2015/4/17.
 */
public class BaseDatadict implements Serializable{
    private static final long serialVersionUID = 6597813796623872169L;
    private String typeName;
    private Integer typeCode;
    private String memo;
    private Integer typeGroup;
    private Date updateTime;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(Integer typeGroup) {
        this.typeGroup = typeGroup;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseDatadict that = (BaseDatadict) o;

        if (typeName != null ? !typeName.equals(that.typeName) : that.typeName != null) return false;
        if (typeCode != null ? !typeCode.equals(that.typeCode) : that.typeCode != null) return false;
        if (memo != null ? !memo.equals(that.memo) : that.memo != null) return false;
        if (typeGroup != null ? !typeGroup.equals(that.typeGroup) : that.typeGroup != null) return false;
        return !(updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null);

    }

    @Override
    public int hashCode() {
        int result = typeName != null ? typeName.hashCode() : 0;
        result = 31 * result + (typeCode != null ? typeCode.hashCode() : 0);
        result = 31 * result + (memo != null ? memo.hashCode() : 0);
        result = 31 * result + (typeGroup != null ? typeGroup.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseDatadict{" +
                "typeName='" + typeName + '\'' +
                ", typeCode=" + typeCode +
                ", memo='" + memo + '\'' +
                ", typeGroup=" + typeGroup +
                ", updateTime=" + updateTime +
                '}';
    }
}