package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;

/**
 * 抽检附件对象
 */
public class SpotCheckAppendixDto implements Serializable {

    private static final long serialVersionUID = 5188416890969219252L;

    /**
     * 附件url
     */
    private String appendixUrl;

    /**
     * 附件类型：1-举报图片 2-举报视频 3-升级图片 4-升级视频
     */
    private Integer appendixType;

    public String getAppendixUrl() {
        return appendixUrl;
    }

    public void setAppendixUrl(String appendixUrl) {
        this.appendixUrl = appendixUrl;
    }

    public Integer getAppendixType() {
        return appendixType;
    }

    public void setAppendixType(Integer appendixType) {
        this.appendixType = appendixType;
    }
}
