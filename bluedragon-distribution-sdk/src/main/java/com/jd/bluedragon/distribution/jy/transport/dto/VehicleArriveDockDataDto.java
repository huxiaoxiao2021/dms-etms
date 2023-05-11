package com.jd.bluedragon.distribution.jy.transport.dto;

import com.jd.bluedragon.distribution.jy.dto.JySelectOption;

import java.io.Serializable;
import java.util.List;

/**
 * 运输车辆靠台基础数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-09 10:41:54 周二
 */
public class VehicleArriveDockDataDto implements Serializable {

    private static final long serialVersionUID = -5941136054594631023L;

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

    /**
     * 月台号
     */
    private String dockCode;

    /**
     * 验证字符，二维码内容
     */
    private String validateStr;

    /**
     * 服务器时间，毫秒单位
     */
    private Long timeMillSeconds;

    /**
     * 服务器时间格式化样式字符
     */
    private String timeStr;

    /**
     * 服务器时间，格式化形式
     */
    private String timeFormatStr;

    /**
     * 验证字符刷新间隔
     */
    private Integer validateStrRefreshIntervalTime;

    public VehicleArriveDockDataDto() {
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

    public String getDockCode() {
        return dockCode;
    }

    public void setDockCode(String dockCode) {
        this.dockCode = dockCode;
    }

    public String getValidateStr() {
        return validateStr;
    }

    public void setValidateStr(String validateStr) {
        this.validateStr = validateStr;
    }

    public Long getTimeMillSeconds() {
        return timeMillSeconds;
    }

    public void setTimeMillSeconds(Long timeMillSeconds) {
        this.timeMillSeconds = timeMillSeconds;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getTimeFormatStr() {
        return timeFormatStr;
    }

    public void setTimeFormatStr(String timeFormatStr) {
        this.timeFormatStr = timeFormatStr;
    }

    public Integer getValidateStrRefreshIntervalTime() {
        return validateStrRefreshIntervalTime;
    }

    public void setValidateStrRefreshIntervalTime(Integer validateStrRefreshIntervalTime) {
        this.validateStrRefreshIntervalTime = validateStrRefreshIntervalTime;
    }
}
