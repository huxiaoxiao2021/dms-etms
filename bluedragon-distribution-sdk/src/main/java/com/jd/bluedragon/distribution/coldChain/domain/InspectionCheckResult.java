package com.jd.bluedragon.distribution.coldChain.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/2
 * @Description:
 */
public class InspectionCheckResult extends ColdCheckCommonResult implements Serializable {

    public static final String ALLIANCE_INTERCEPT_MESSAGE = "加盟商预付款余额不足，请联系加盟商处理!";
    public static final int ERROR_CODE = 500;

    static final long serialVersionUID = 1L;

    /*库位号*/
    private String storageCode;

    /*PDA验货提示语*/
    private String hintMessage;

    /*路由下一节点*/
    private String nextRouterSiteName;

    /**
     * 笼车号
     */
    private String tabletrolleyCode = "";


    /**
     * 包裹总数
     */
    private int packageSize;

    /**
     * 已扫包裹总数
     */
    private int sacnPackageSize;

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getHintMessage() {
        return hintMessage;
    }

    public void setHintMessage(String hintMessage) {
        this.hintMessage = hintMessage;
    }

    public String getNextRouterSiteName() {
        return nextRouterSiteName;
    }

    public void setNextRouterSiteName(String nextRouterSiteName) {
        this.nextRouterSiteName = nextRouterSiteName;
    }

    public String getTabletrolleyCode() {
        return tabletrolleyCode;
    }

    public void setTabletrolleyCode(String tabletrolleyCode) {
        this.tabletrolleyCode = tabletrolleyCode;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public int getSacnPackageSize() {
        return sacnPackageSize;
    }

    public void setSacnPackageSize(int sacnPackageSize) {
        this.sacnPackageSize = sacnPackageSize;
    }
}
