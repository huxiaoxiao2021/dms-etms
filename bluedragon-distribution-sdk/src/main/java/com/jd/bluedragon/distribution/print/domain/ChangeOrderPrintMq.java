package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/20 10:35
 * @Description: 换单打印mq
 */
public class ChangeOrderPrintMq implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 业务操作类型
     */
    private Integer operateType;
    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 补打的包裹
     */
    private List<String> reprintPackages;

    /**
     * 操作人
     */
    private Integer userCode;

    /**
     * 操作人name
     */
    private String userName;

    /**
     * 操作人erp
     */
    private String userErp;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 站点编号
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;


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

    public List<String> getReprintPackages() {
        return reprintPackages;
    }

    public void setReprintPackages(List<String> reprintPackages) {
        this.reprintPackages = reprintPackages;
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

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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
}
