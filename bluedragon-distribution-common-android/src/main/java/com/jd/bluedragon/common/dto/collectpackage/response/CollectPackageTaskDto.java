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
    private String mixBoxTypeDesc;

    /**
     * 运输类型
     */
    private Integer transportType;
    
    /**
     * 运输类型描述
     */
    private String transportTypeDesc;
    
    private Long startSiteId;

    private String startSiteName;

    /**
     * 目的地流向集合
     */
    private List<CollectPackageFlowDto> collectPackageFlowDtoList;
    
    private String createUserErp;

    private String createUserName;
    
    private String materialCode;

    /**
     * 拦截件数
     */
    private Integer interceptCount = 0;

    /**
     * 扫描件数
     */
    private Integer scanCount = 0;

    /**
     * 箱子是否已经被放入LL箱子中
     */
    private boolean hasBoundBoxFlag;


    /**
     * 已扫包裹数量
     */
    private Integer packageScanCount;

    /**
     * 已扫箱号数量
     */
    private Integer boxScanCount;


    /**
     * 拦截包裹数量
     */
    private Integer packageInterceptCount;

    /**
     * 拦截箱号数量
     */
    private Integer boxInterceptCount;


    /**
     * 箱号绑定的循环物资类型
     */
    private int materialType;


    /**
     * 任务目的地id
     */
    private Long endSiteId;

    /**
     * 任务目的地名称
     */
    private String endSiteName;

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public int getMaterialType(){
        return materialType;
    }

    public void setMaterialType(int materialType) {
        this.materialType = materialType;
    }

    public Integer getPackageScanCount() {
        return packageScanCount;
    }

    public void setPackageScanCount(Integer packageScanCount) {
        this.packageScanCount = packageScanCount;
    }

    public Integer getBoxScanCount() {
        return boxScanCount;
    }

    public void setBoxScanCount(Integer boxScanCount) {
        this.boxScanCount = boxScanCount;
    }

    public Integer getPackageInterceptCount() {
        return packageInterceptCount;
    }

    public void setPackageInterceptCount(Integer packageInterceptCount) {
        this.packageInterceptCount = packageInterceptCount;
    }

    public Integer getBoxInterceptCount() {
        return boxInterceptCount;
    }

    public void setBoxInterceptCount(Integer boxInterceptCount) {
        this.boxInterceptCount = boxInterceptCount;
    }

    public boolean getHasBoundBoxFlag() {
        return hasBoundBoxFlag;
    }

    public void setHasBoundBoxFlag(boolean hasBoundBoxFlag) {
        this.hasBoundBoxFlag = hasBoundBoxFlag;
    }

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

    public String getMixBoxTypeDesc() {
        return mixBoxTypeDesc;
    }

    public void setMixBoxTypeDesc(String mixBoxTypeDesc) {
        this.mixBoxTypeDesc = mixBoxTypeDesc;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getTransportTypeDesc() {
        return transportTypeDesc;
    }

    public void setTransportTypeDesc(String transportTypeDesc) {
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

    public List<CollectPackageFlowDto> getCollectPackageFlowDtoList() {
        return collectPackageFlowDtoList;
    }

    public void setCollectPackageFlowDtoList(List<CollectPackageFlowDto> collectPackageFlowDtoList) {
        this.collectPackageFlowDtoList = collectPackageFlowDtoList;
    }
}
