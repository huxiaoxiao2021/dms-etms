package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 滑道号信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-09 16:49:52 周天
 */
public class CrossPackageTagNew implements Serializable {

    private static final long serialVersionUID = -7526237147920496099L;

    private Long id;

    private String originalDmsName;

    private String destinationDmsName;

    private String targetSiteName;

    private String originalCrossCode;

    private String originalTabletrolleyCode;

    private String destinationCrossCode;

    private String destinationTabletrolleyCode;

    private Integer isZiTi;

    private String printSiteName;

    private String printAddress;

    private Integer isAirTransport;

    private Long businessId;

    private Long sysId;

    private Integer isDescend;

    private Long oldId;

    private Integer storeOrg;

    private Integer originalCrossType;

    private Integer destinationCrossType;


    private String storeType;

    private Integer storeId;

    private Integer cky2;

    private Integer originalDmsId;

    private Integer targetSiteId;

    private String jsonData;

    private Integer tagType;

    private Date descendTime;

    private Integer destinationDmsId;

    private Date createTime;

    private Date updateTime;

    private String createOperatorName;

    private String updateOperatorName;

    private String remark;

    private int yn;
    private int startIndex;
    private int endIndex;

    public Long getId() {
        return id;
    }

    public CrossPackageTagNew setId(Long id) {
        this.id = id;
        return this;
    }

    public String getOriginalDmsName() {
        return originalDmsName;
    }

    public CrossPackageTagNew setOriginalDmsName(String originalDmsName) {
        this.originalDmsName = originalDmsName;
        return this;
    }

    public String getDestinationDmsName() {
        return destinationDmsName;
    }

    public CrossPackageTagNew setDestinationDmsName(String destinationDmsName) {
        this.destinationDmsName = destinationDmsName;
        return this;
    }

    public String getTargetSiteName() {
        return targetSiteName;
    }

    public CrossPackageTagNew setTargetSiteName(String targetSiteName) {
        this.targetSiteName = targetSiteName;
        return this;
    }

    public String getOriginalCrossCode() {
        return originalCrossCode;
    }

    public CrossPackageTagNew setOriginalCrossCode(String originalCrossCode) {
        this.originalCrossCode = originalCrossCode;
        return this;
    }

    public String getOriginalTabletrolleyCode() {
        return originalTabletrolleyCode;
    }

    public CrossPackageTagNew setOriginalTabletrolleyCode(String originalTabletrolleyCode) {
        this.originalTabletrolleyCode = originalTabletrolleyCode;
        return this;
    }

    public String getDestinationCrossCode() {
        return destinationCrossCode;
    }

    public CrossPackageTagNew setDestinationCrossCode(String destinationCrossCode) {
        this.destinationCrossCode = destinationCrossCode;
        return this;
    }

    public String getDestinationTabletrolleyCode() {
        return destinationTabletrolleyCode;
    }

    public CrossPackageTagNew setDestinationTabletrolleyCode(String destinationTabletrolleyCode) {
        this.destinationTabletrolleyCode = destinationTabletrolleyCode;
        return this;
    }

    public Integer getIsZiTi() {
        return isZiTi;
    }

    public CrossPackageTagNew setIsZiTi(Integer isZiTi) {
        this.isZiTi = isZiTi;
        return this;
    }

    public String getPrintSiteName() {
        return printSiteName;
    }

    public CrossPackageTagNew setPrintSiteName(String printSiteName) {
        this.printSiteName = printSiteName;
        return this;
    }

    public String getPrintAddress() {
        return printAddress;
    }

    public CrossPackageTagNew setPrintAddress(String printAddress) {
        this.printAddress = printAddress;
        return this;
    }

    public Integer getIsAirTransport() {
        return isAirTransport;
    }

    public CrossPackageTagNew setIsAirTransport(Integer isAirTransport) {
        this.isAirTransport = isAirTransport;
        return this;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public CrossPackageTagNew setBusinessId(Long businessId) {
        this.businessId = businessId;
        return this;
    }

    public Long getSysId() {
        return sysId;
    }

    public CrossPackageTagNew setSysId(Long sysId) {
        this.sysId = sysId;
        return this;
    }

    public Integer getIsDescend() {
        return isDescend;
    }

    public CrossPackageTagNew setIsDescend(Integer isDescend) {
        this.isDescend = isDescend;
        return this;
    }

    public Long getOldId() {
        return oldId;
    }

    public CrossPackageTagNew setOldId(Long oldId) {
        this.oldId = oldId;
        return this;
    }

    public Integer getStoreOrg() {
        return storeOrg;
    }

    public CrossPackageTagNew setStoreOrg(Integer storeOrg) {
        this.storeOrg = storeOrg;
        return this;
    }

    public Integer getOriginalCrossType() {
        return originalCrossType;
    }

    public CrossPackageTagNew setOriginalCrossType(Integer originalCrossType) {
        this.originalCrossType = originalCrossType;
        return this;
    }

    public Integer getDestinationCrossType() {
        return destinationCrossType;
    }

    public CrossPackageTagNew setDestinationCrossType(Integer destinationCrossType) {
        this.destinationCrossType = destinationCrossType;
        return this;
    }

    public String getStoreType() {
        return storeType;
    }

    public CrossPackageTagNew setStoreType(String storeType) {
        this.storeType = storeType;
        return this;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public CrossPackageTagNew setStoreId(Integer storeId) {
        this.storeId = storeId;
        return this;
    }

    public Integer getCky2() {
        return cky2;
    }

    public CrossPackageTagNew setCky2(Integer cky2) {
        this.cky2 = cky2;
        return this;
    }

    public Integer getOriginalDmsId() {
        return originalDmsId;
    }

    public CrossPackageTagNew setOriginalDmsId(Integer originalDmsId) {
        this.originalDmsId = originalDmsId;
        return this;
    }

    public Integer getTargetSiteId() {
        return targetSiteId;
    }

    public CrossPackageTagNew setTargetSiteId(Integer targetSiteId) {
        this.targetSiteId = targetSiteId;
        return this;
    }

    public String getJsonData() {
        return jsonData;
    }

    public CrossPackageTagNew setJsonData(String jsonData) {
        this.jsonData = jsonData;
        return this;
    }

    public Integer getTagType() {
        return tagType;
    }

    public CrossPackageTagNew setTagType(Integer tagType) {
        this.tagType = tagType;
        return this;
    }

    public Date getDescendTime() {
        return descendTime;
    }

    public CrossPackageTagNew setDescendTime(Date descendTime) {
        this.descendTime = descendTime;
        return this;
    }

    public Integer getDestinationDmsId() {
        return destinationDmsId;
    }

    public CrossPackageTagNew setDestinationDmsId(Integer destinationDmsId) {
        this.destinationDmsId = destinationDmsId;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public CrossPackageTagNew setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public CrossPackageTagNew setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public CrossPackageTagNew setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
        return this;
    }

    public String getUpdateOperatorName() {
        return updateOperatorName;
    }

    public CrossPackageTagNew setUpdateOperatorName(String updateOperatorName) {
        this.updateOperatorName = updateOperatorName;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public CrossPackageTagNew setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public int getYn() {
        return yn;
    }

    public CrossPackageTagNew setYn(int yn) {
        this.yn = yn;
        return this;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public CrossPackageTagNew setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public CrossPackageTagNew setEndIndex(int endIndex) {
        this.endIndex = endIndex;
        return this;
    }
}
