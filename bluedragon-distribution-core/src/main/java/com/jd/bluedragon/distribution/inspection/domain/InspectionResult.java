package com.jd.bluedragon.distribution.inspection.domain;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by hujiping on 2018/3/21.
 */
public class InspectionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /*库位号*/
    private String storageCode;

    /*PDA验货提示语*/
    private String hintMessage;

    /*路由下一节点*/
    private String nextRouterSiteName;

    /**
     * 笼车号
     */
    private String tabletrolleyCode = StringUtils.EMPTY;

    public String getNextRouterSiteName() {
        return nextRouterSiteName;
    }

    public void setNextRouterSiteName(String nextRouterSiteName) {
        this.nextRouterSiteName = nextRouterSiteName;
    }

    public InspectionResult(String storageCode) {
        this.storageCode = storageCode;
    }

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

    public String getTabletrolleyCode() {
        return tabletrolleyCode;
    }

    public void setTabletrolleyCode(String tabletrolleyCode) {
        this.tabletrolleyCode = tabletrolleyCode;
    }
}
