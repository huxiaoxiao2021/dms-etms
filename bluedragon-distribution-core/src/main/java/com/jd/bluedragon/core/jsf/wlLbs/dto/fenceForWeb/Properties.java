
package com.jd.bluedragon.core.jsf.wlLbs.dto.fenceForWeb;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Properties implements Serializable {

    private static final long serialVersionUID = 267886657866923058L;

    private String areaShape;

    private String areaSource;

    private String areaType;

    private Long createTime;

    private String createUserCode;

    private String createUserName;

    private Long effectiveBeginTime;

    private Long effectiveEndTime;

    private String fenceType;

    private Long id;

    private Integer isCircle;

    private Integer isDelete;

    private BigDecimal lat;

    private BigDecimal lng;

    private String nodeCode;

    private String nodeDataSource;

    private String nodeName;

    private String orgCode;

    private Long updateTime;

    private String updateUserCode;

    private String updateUserName;

    public String getAreaShape() {
        return areaShape;
    }

    public void setAreaShape(String areaShape) {
        this.areaShape = areaShape;
    }

    public String getAreaSource() {
        return areaSource;
    }

    public void setAreaSource(String areaSource) {
        this.areaSource = areaSource;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Long getEffectiveBeginTime() {
        return effectiveBeginTime;
    }

    public void setEffectiveBeginTime(Long effectiveBeginTime) {
        this.effectiveBeginTime = effectiveBeginTime;
    }

    public Long getEffectiveEndTime() {
        return effectiveEndTime;
    }

    public void setEffectiveEndTime(Long effectiveEndTime) {
        this.effectiveEndTime = effectiveEndTime;
    }

    public String getFenceType() {
        return fenceType;
    }

    public void setFenceType(String fenceType) {
        this.fenceType = fenceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsCircle() {
        return isCircle;
    }

    public void setIsCircle(Integer isCircle) {
        this.isCircle = isCircle;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeDataSource() {
        return nodeDataSource;
    }

    public void setNodeDataSource(String nodeDataSource) {
        this.nodeDataSource = nodeDataSource;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(String updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

}
