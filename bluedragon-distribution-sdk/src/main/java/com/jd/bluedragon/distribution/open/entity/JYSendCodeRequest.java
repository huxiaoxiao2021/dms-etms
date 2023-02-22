package com.jd.bluedragon.distribution.open.entity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: JYSendCodeRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 10:14
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class JYSendCodeRequest {

    /**
     * 接口调用信息
     */
    private RequestProfile requestProfile;

    /**
     * 创建批次的操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 批次始发场地
     */
    private String departSiteCode;

    /**
     * 批次目的场地
     */
    private String arriveSiteCode;

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public String getDepartSiteCode() {
        return departSiteCode;
    }

    public void setDepartSiteCode(String departSiteCode) {
        this.departSiteCode = departSiteCode;
    }

    public String getArriveSiteCode() {
        return arriveSiteCode;
    }

    public void setArriveSiteCode(String arriveSiteCode) {
        this.arriveSiteCode = arriveSiteCode;
    }
}
