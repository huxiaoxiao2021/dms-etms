package com.jd.bluedragon.common.dto.inspection.request;

import java.io.Serializable;

/**
 * @ClassName InspectionRequest
 * @Description
 * @Author wyh
 * @Date 2022/2/10 20:23
 **/
public class InspectionRequest implements Serializable {

    private static final long serialVersionUID = -4305293341631044606L;

    private String barCode;

    private int businessType;

    private int createSiteCode;

    private String createSiteName;

    private String operateTime;

    private int operateType;

    private int operateUserCode;

    private String operateUserName;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public int getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(int createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public int getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(int operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }
}
