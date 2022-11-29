package com.jd.bluedragon.common.dto.voice.request;

import java.io.Serializable;

/**
 * 提示语请求体
 *
 * @author hujiping
 * @date 2022/10/26 5:18 PM
 */
public class HintVoiceReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本号
     */
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
