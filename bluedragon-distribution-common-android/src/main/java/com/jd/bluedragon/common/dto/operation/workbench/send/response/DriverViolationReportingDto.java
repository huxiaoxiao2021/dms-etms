package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 司机违规举报DTO
 * @date 2024/4/12
 */
public class DriverViolationReportingDto implements Serializable {
    /**
     * 查询标识，true：查看数据，false：无数据，提交数据
     */
    private Boolean queryFlag;
    /**
     * 图片ulr集合
     */
    private List<String> imgUrlList;
    /**
     * 视频url
     */
    private String videoUrl;

    public Boolean getQueryFlag() {
        return queryFlag;
    }

    public void setQueryFlag(Boolean queryFlag) {
        this.queryFlag = queryFlag;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
