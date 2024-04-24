package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author pengchong28
 * @description 组板任务司机违规举报DTO
 * @date 2024/4/16
 */
public class JyDriverViolationReportingDto implements Serializable {
    private String bizId;

    private String videoUrl;

    private List<String> imgUrlList;

    /**
     * 操作场地编码
     */
    private Long siteCode;
    /**
     * 创建时间
     */
    private Date createTime;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
