
package com.jd.bluedragon.distribution.jy.transport.dto;

import java.io.Serializable;

/**
 * 运输车辆到达靠台结果对象
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-08 20:50:02 周一
 */
public class VehicleArriveDockResponseDto implements Serializable {

    private static final long serialVersionUID = 4824231645421004431L;
    /**
     * 是否校验通过
     */
    private Boolean legal;

    /**
     * 校验信息
     */
    private String validateMsg;

    /**
     * 月台号
     */
    private String dockCode;

    /**
     * 场地编码
     */
    private String siteCode;

    /**
     * 场地ID
     */
    private Integer siteId;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 作业区编码
     */
    private String workAreaCode;

    /**
     * 作业区名称
     */
    private String workAreaName;

    /**
     * 网格编号
     */
    private String workGridNo;

    /**
     * 网格名称
     */
    private String workGridName;

    public VehicleArriveDockResponseDto() {
    }

    public Boolean getLegal() {
        return legal;
    }

    public void setLegal(Boolean legal) {
        this.legal = legal;
    }

    public String getValidateMsg() {
        return validateMsg;
    }

    public void setValidateMsg(String validateMsg) {
        this.validateMsg = validateMsg;
    }

    public String getDockCode() {
        return dockCode;
    }

    public void setDockCode(String dockCode) {
        this.dockCode = dockCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getWorkAreaCode() {
        return workAreaCode;
    }

    public void setWorkAreaCode(String workAreaCode) {
        this.workAreaCode = workAreaCode;
    }

    public String getWorkAreaName() {
        return workAreaName;
    }

    public void setWorkAreaName(String workAreaName) {
        this.workAreaName = workAreaName;
    }

    public String getWorkGridNo() {
        return workGridNo;
    }

    public void setWorkGridNo(String workGridNo) {
        this.workGridNo = workGridNo;
    }

    public String getWorkGridName() {
        return workGridName;
    }

    public void setWorkGridName(String workGridName) {
        this.workGridName = workGridName;
    }
}
