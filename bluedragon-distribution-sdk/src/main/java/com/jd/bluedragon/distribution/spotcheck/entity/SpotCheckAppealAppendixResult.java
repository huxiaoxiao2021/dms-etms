package com.jd.bluedragon.distribution.spotcheck.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 抽检附件对象
 */
public class SpotCheckAppealAppendixResult implements Serializable {

    private static final long serialVersionUID = 5188416890969219252L;

    /**
     * 附件类型：0-文档,1-图片,2-音频,3-视频
     */
    private Integer code;

    /**
     * 附件url列表
     */
    private List<String> appendixUrls;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<String> getAppendixUrls() {
        return appendixUrls;
    }

    public void setAppendixUrls(List<String> appendixUrls) {
        this.appendixUrls = appendixUrls;
    }
}
