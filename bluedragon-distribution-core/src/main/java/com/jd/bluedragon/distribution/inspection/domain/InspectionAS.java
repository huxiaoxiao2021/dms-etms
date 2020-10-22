package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;

/**
 * 自动分拣机交接验货表
 * Created by dudong on 2014/10/20.
 */
public class InspectionAS implements Serializable {
    private static final long serialVersionUID = -7623509285189482980L;

    /**
     * 封签号
     */
    public String sealBoxCode;

    /**
     * 箱号
     */
    public String boxCode;

    /**
     * 包裹号
     */
    public String packageBarOrWaybillCode;

    /**
     * 异常类型
     */
    public String exceptionType;

    /**
     * 验货操作类型
     */
    public int operateType;

    /**
     * 收货单位（只有分拣助手使用，正逆向不使用，需要赋值为String.Empty） *
     */
    public int receiveSiteCode;

    /**
     * 主键ID
     */
    public long id;

    /**
     * 业务类型:‘10’正向  "20' 逆向 '30' 三方
     */
    public int businessType;

    /**
     * 操作人编号
     */
    public int userCode;

    /**
     * 操作人姓名
     */
    public String userName;

    /**
     * 操作单位编号（分拣中心ID）
     */
    public int siteCode;

    /**
     * 操作单位名称（分拣中心名称）
     */
    public String siteName;

    /**
     * 操作时间
     */
    public String operateTime;

    /**
     * 操作来源
     */
    public Integer bizSource;

    public String getSealBoxCode() {
        return sealBoxCode;
    }

    public void setSealBoxCode(String sealBoxCode) {
        this.sealBoxCode = sealBoxCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageBarOrWaybillCode() {
        return packageBarOrWaybillCode;
    }

    public void setPackageBarOrWaybillCode(String packageBarOrWaybillCode) {
        this.packageBarOrWaybillCode = packageBarOrWaybillCode;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public int getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(int receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
