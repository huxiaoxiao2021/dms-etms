package com.jd.bluedragon.distribution.packageWeighting.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jinjingcheng on 2018/4/22.
 */
public class PackageWeighting implements Serializable{
    private static final long serialVersionUID = 6769642707170655534L;

    private Integer businessType; //量方称重数据来源  1-分拣  2-接货仓配送员接货  3-接货中心驻场操作  4-仓库操作  5-车队操作
    private String businessDesc;  //操作类型描述
    private Integer operateType; //1001-分拣包裹称重  1002 –分拣B网运单称重
    private String opeDesc;      //操作类型描述
    private String waybillCode; //运单号
    private String packageCode; //包裹号
    private Double weight;      //重量
    private Double volume;      //体积
    private Double length;      //长
    private Double width;       //宽
    private Double height;      //高

    private Long weightUserCode;   //操作人编码
    private String weightUserErp;     //操作人erp
    private String weightUserName;    //操作人姓名

    private Long measureUserCode;   //操作人编码
    private String measureUserErp;     //操作人erp
    private String measureUserName;    //操作人姓名

    private Integer siteCode;   //操作单位编码
    private String siteName;   //操作单位名称

    private Date weightTime;   //称重时间
    private Date measureTime;   //操作时间

    private Date createTime;    //创建时间
    private Date updateTime;    //更新时间
    private Integer isDelete;  //删除标识
    private String dmsDisCode; //目标分拣中心

    private Integer createSiteCode; //称重量方操作场地编号
    private String createSiteName; //称重量方操作场地名称
    /**
     * 当前包裹称重量方数据归属的表名
     */
    private String tableName;

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Long getWeightUserCode() {
        return weightUserCode;
    }

    public void setWeightUserCode(Long weightUserCode) {
        this.weightUserCode = weightUserCode;
    }

    public String getWeightUserErp() {
        return weightUserErp;
    }

    public void setWeightUserErp(String weightUserErp) {
        this.weightUserErp = weightUserErp;
    }

    public String getWeightUserName() {
        return weightUserName;
    }

    public void setWeightUserName(String weightUserName) {
        this.weightUserName = weightUserName;
    }

    public Long getMeasureUserCode() {
        return measureUserCode;
    }

    public void setMeasureUserCode(Long measureUserCode) {
        this.measureUserCode = measureUserCode;
    }

    public String getMeasureUserErp() {
        return measureUserErp;
    }

    public void setMeasureUserErp(String measureUserErp) {
        this.measureUserErp = measureUserErp;
    }

    public String getMeasureUserName() {
        return measureUserName;
    }

    public void setMeasureUserName(String measureUserName) {
        this.measureUserName = measureUserName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Date getWeightTime() {
        return weightTime;
    }

    public void setWeightTime(Date weightTime) {
        this.weightTime = weightTime;
    }

    public Date getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getDmsDisCode() {
        return dmsDisCode;
    }

    public void setDmsDisCode(String dmsDisCode) {
        this.dmsDisCode = dmsDisCode;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public String getOpeDesc() {
        return opeDesc;
    }

    public void setOpeDesc(String opeDesc) {
        this.opeDesc = opeDesc;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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
}
