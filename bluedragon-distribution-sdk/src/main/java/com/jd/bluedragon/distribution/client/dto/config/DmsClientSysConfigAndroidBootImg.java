
package com.jd.bluedragon.distribution.client.dto.config;


import java.io.Serializable;

/**
 * 分拣安卓启动画面配置
 * @author fanggang7
 * @time 2023-05-08 20:47:41 周一
 */
public class DmsClientSysConfigAndroidBootImg implements Serializable {

    private static final long serialVersionUID = -8258130277665460531L;

    /**
     * 显示时间，毫秒单位
     */
    private Long displayTime;

    /**
     * 生效开始时间
     */
    private String enableTimStart;

    /**
     * 生效结束时间
     */
    private String enableTimeEnd;

    /**
     * 跳转链接
     */
    private String link;

    /**
     * 图片下载地址
     */
    private String url;

    /**
     * 版本号
     */
    private Integer version;

    public DmsClientSysConfigAndroidBootImg() {}

    public Long getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(Long displayTime) {
        this.displayTime = displayTime;
    }

    public String getEnableTimStart() {
        return enableTimStart;
    }

    public void setEnableTimStart(String enableTimStart) {
        this.enableTimStart = enableTimStart;
    }

    public String getEnableTimeEnd() {
        return enableTimeEnd;
    }

    public void setEnableTimeEnd(String enableTimeEnd) {
        this.enableTimeEnd = enableTimeEnd;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
