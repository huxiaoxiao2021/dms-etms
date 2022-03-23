package com.jd.bluedragon.distribution.recycle.material.domain;

import java.io.Serializable;

/**
 * 循环周转筐
 */
public class RecycleBasketEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer createSiteCode;

    public String createSiteName;

    public Integer userCode;

    public String userErp;

    public String userName;

    public Integer quantity;
    /**
     * 打印类型 1：打印 2：补打
     */
    public Integer printType;

    public String recycleBasketCode;

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

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrintType() {
        return printType;
    }

    public void setPrintType(Integer printType) {
        this.printType = printType;
    }

    public String getRecycleBasketCode() {
        return recycleBasketCode;
    }

    public void setRecycleBasketCode(String recycleBasketCode) {
        this.recycleBasketCode = recycleBasketCode;
    }
}
