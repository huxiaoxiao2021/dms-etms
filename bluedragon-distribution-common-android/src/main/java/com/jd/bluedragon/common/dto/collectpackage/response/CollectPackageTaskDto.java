package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description 集包任务
 * @date 2023-10-12 14:12
 */
public class CollectPackageTaskDto implements Serializable {

    private String bizId;

    private String boxCode;

    private Integer taskStatus;

    private String boxType;

    /**
     * 箱号类型描述
     */
    private String boxTypeDesc;

    /**
     * 混包类型
     */
    private Integer mixBoxType;
    
    /**
     * 混包类型描述
     */
    private Integer mixBoxTypeDesc;

    /**
     * 运输类型
     */
    private Integer transportType;
    
    /**
     * 运输类型描述
     */
    private Integer transportTypeDesc;
    
    private Long startSiteId;

    private String startSiteName;

    /**
     * 目的地流向集合
     */
    private List<CollectPackageFlowDto> endSiteList;
    
    private String createUserErp;

    private String createUserName;
    
    private String materialCode;

    /**
     * 拦截件数
     */
    private Integer interceptCount;

    /**
     * 扫描件数
     */
    private Integer scanCount;
    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getBoxTypeDesc() {
        return boxTypeDesc;
    }

    public void setBoxTypeDesc(String boxTypeDesc) {
        this.boxTypeDesc = boxTypeDesc;
    }

    public Integer getMixBoxType() {
        return mixBoxType;
    }

    public void setMixBoxType(Integer mixBoxType) {
        this.mixBoxType = mixBoxType;
    }

    public Integer getMixBoxTypeDesc() {
        return mixBoxTypeDesc;
    }

    public void setMixBoxTypeDesc(Integer mixBoxTypeDesc) {
        this.mixBoxTypeDesc = mixBoxTypeDesc;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public Integer getTransportTypeDesc() {
        return transportTypeDesc;
    }

    public void setTransportTypeDesc(Integer transportTypeDesc) {
        this.transportTypeDesc = transportTypeDesc;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
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

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getScanCount() {
        return scanCount;
    }

    public void setScanCount(Integer scanCount) {
        this.scanCount = scanCount;
    }

    public List<CollectPackageFlowDto> getEndSiteList() {
        return endSiteList;
    }

    public void setEndSiteList(List<CollectPackageFlowDto> endSiteList) {
        this.endSiteList = endSiteList;
    }
}
