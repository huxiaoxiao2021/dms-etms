package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
import java.util.Date;

public class BoxPackageDto implements Serializable {
    private static final long serialVersionUID = -9180685162472511668L;

    /**
     *  运单号
     */
    private String waybillCode;
    /**
     *  包裹号
     */
    private String packageCode;

    private String userErp;

    /**
     *  操作用户
     */
    private Integer userCode;
    /**
     *  操作用户名
     */
    private String userName;
    /**
     *  操作时间:yyyy-MM-dd HH:mm:ss
     */
    private Date opeateTime;
    /**
     *  操作类型， 10 绑定  20 解绑
     */
    private Integer opreateType;

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

    public Date getOpeateTime() {
        return opeateTime;
    }

    public void setOpeateTime(Date opeateTime) {
        this.opeateTime = opeateTime;
    }

    public Integer getOpreateType() {
        return opreateType;
    }

    public void setOpreateType(Integer opreateType) {
        this.opreateType = opreateType;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
