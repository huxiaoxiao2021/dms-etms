package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务列表响应结果
 * @author: wuming
 * @create: 2020-10-14 17:27
 */
public class LoadTaskListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌
     */
    private String licenseNumber;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 装车任务状态：装车任务状态：0-未开始，1-已开始，2-已完成
     */
    private Integer status;

    /**
     * 任务Id
     */
    private Long id;

    private Long endSiteCode;

    /**
     * 操作时间
     */
    private Date updateTime;

    public LoadTaskListDto() {
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(Long endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
