package com.jd.bluedragon.distribution.cage.request;


import java.io.Serializable;
public class CollectPackageReq implements Serializable {
    private static final long serialVersionUID = 1524434357342779618L;

    private String barCode;
    private String boxCode;
    /**
     *操作单位编号
     */
    private Long siteCode;

    private String userErp;

    private String userName;


    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
