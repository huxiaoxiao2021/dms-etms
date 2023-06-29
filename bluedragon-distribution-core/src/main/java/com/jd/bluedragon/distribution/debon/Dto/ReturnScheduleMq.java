package com.jd.bluedragon.distribution.debon.Dto;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/26 10:40
 * @Description: 包裹返调度MQ
 */
public class ReturnScheduleMq implements Serializable {


    /* 包裹号 */
    private String packageBarcode;

    /* 运单地址 */
    private String address;

    /* 预分拣目的站点编号 */
    private Integer receiveSiteCode;

    /* 预分拣目的站点名称 */
    private String receiveSiteName;

    /* 现场调度站点编号 */
    private Integer changeSiteCode;

    /* 现场调度站点名称 */
    private String changeSiteName;

    /* PDA操作时间 */
    private Date operateTime;

    /* 操作人编号_ERP帐号 */
    private Integer userCode;

    /* 操作人姓名 */
    private String userName;

    /* 操作人所属站点编号 */
    private Integer siteCode;

    /* 操作人所属站点编号 */
    private String siteName;

    private String waybillCode;

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getChangeSiteCode() {
        return changeSiteCode;
    }

    public void setChangeSiteCode(Integer changeSiteCode) {
        this.changeSiteCode = changeSiteCode;
    }

    public String getChangeSiteName() {
        return changeSiteName;
    }

    public void setChangeSiteName(String changeSiteName) {
        this.changeSiteName = changeSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
