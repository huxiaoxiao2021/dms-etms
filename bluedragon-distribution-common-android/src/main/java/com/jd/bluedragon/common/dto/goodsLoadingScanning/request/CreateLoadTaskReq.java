package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;
import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 新建装车任务请求参数
 * @author: wuming
 * @create: 2020-10-14 15:35
 */
public class CreateLoadTaskReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌
     */
    private String licenseNumber;

    /**
     * 目的场地Id
     */
    private Long endSiteCode;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 创建人所属转运中心Id
     */
    private Long currentSiteCode;

    /**
     * 创建人所属转运中心名称
     */
    private String currentSiteName;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 协助人信息
     */
    private List<AssistorInfoReq> assistorInfo;

    public CreateLoadTaskReq() {
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Long getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(Long endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Long getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Long currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public String getCurrentSiteName() {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName) {
        this.currentSiteName = currentSiteName;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public List<AssistorInfoReq> getAssistorInfo() {
        return assistorInfo;
    }

    public void setAssistorInfo(List<AssistorInfoReq> assistorInfo) {
        this.assistorInfo = assistorInfo;
    }

}
