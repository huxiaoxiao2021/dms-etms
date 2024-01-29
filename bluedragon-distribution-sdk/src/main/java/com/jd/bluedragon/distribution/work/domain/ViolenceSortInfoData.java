package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;

/**
 * 暴力分拣任务个性化信息
 */
public class ViolenceSortInfoData implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 任务详情页标题 **/
    private String title;
    /** 触发时间 **/
    private String createTime;
    /** 设备名称 **/
    private String deviceName;
    /** 视频链接 **/
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
