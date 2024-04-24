package com.jd.bluedragon.distribution.jy.dto.task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author pengchong28
 * @description 司机违规上报质控mq实体
 * @date 2024/4/23
 */
public class DriverViolationReportingQualityMq implements Serializable {
    /**
     * 上报日期
     */
    private Date reportingDate;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * TW派车任务号
     */
    private String transWorkItemCode;
    /**
     * 举报的场地ID
     */
    private Long reportingSiteCode;
    /**
     * 下游场地ID
     */
    private Long endSiteId;
    /**
     * 视频url
     */
    private String videoUrl;
    /**
     * 图片url
     */
    private List<String> imgUrl;

    public Date getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(Date reportingDate) {
        this.reportingDate = reportingDate;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public Long getReportingSiteCode() {
        return reportingSiteCode;
    }

    public void setReportingSiteCode(Long reportingSiteCode) {
        this.reportingSiteCode = reportingSiteCode;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }
}
