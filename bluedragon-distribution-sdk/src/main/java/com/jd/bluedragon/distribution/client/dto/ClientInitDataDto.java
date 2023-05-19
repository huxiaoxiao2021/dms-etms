package com.jd.bluedragon.distribution.client.dto;

import com.jd.bluedragon.distribution.client.dto.config.DmsClientSysConfigAndroidBootImg;

import java.io.Serializable;
import java.util.List;

/**
 * 安卓初始化数据
 */
public class ClientInitDataDto implements Serializable {

    private static final long serialVersionUID = -8612417622023951677L;

    /**
     * 启动画面
     */
    private DmsClientSysConfigAndroidBootImg bootImg;

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

    public DmsClientSysConfigAndroidBootImg getBootImg() {
        return bootImg;
    }

    public void setBootImg(DmsClientSysConfigAndroidBootImg bootImg) {
        this.bootImg = bootImg;
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
}
