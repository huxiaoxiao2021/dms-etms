package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 快运改址打印取消拦截消息体
 *
 * @author hujiping
 * @date 2022/12/14 3:41 PM
 */
public class KyAddressModifyPrintCancelInterceptMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    // 运单号
    private String waybillCode;

    // 包裹号
    private String packageCode;

    // 操作时间
    private Long operateTime;

    // 打印类型
    private Integer printType;

    // 操作人id
    private Integer userCode;

    // 操作人姓名
    private String userName;

    // 操作人erp
    private String userErp;

    // 操作站点
    private Integer siteCode;

    // 操作站点名称
    private String siteName;

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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getPrintType() {
        return printType;
    }

    public void setPrintType(Integer printType) {
        this.printType = printType;
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
