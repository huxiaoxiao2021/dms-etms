package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @author lijie
 * @date 2020/6/18 16:07
 */
public class HintCheckRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String packageCode;

    private int createSiteCode;

    private boolean newInspectionCheck = false;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public int getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(int createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public boolean getNewInspectionCheck() {
        return newInspectionCheck;
    }

    public void setNewInspectionCheck(boolean newInspectionCheck) {
        this.newInspectionCheck = newInspectionCheck;
    }
}
