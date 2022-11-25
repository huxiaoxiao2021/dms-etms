package com.jd.bluedragon.common.dto.voice.response;

import java.io.Serializable;
import java.util.List;

/**
 * 提示音返回体VO
 *
 * @author hujiping
 * @date 2022/10/26 5:19 PM
 */
public class HintVoiceResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本号
     */
    private String version;

    /**
     * 提示音配置
     */
    private List<HintVoiceConfig> hintVoiceConfigList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<HintVoiceConfig> getHintVoiceConfigList() {
        return hintVoiceConfigList;
    }

    public void setHintVoiceConfigList(List<HintVoiceConfig> hintVoiceConfigList) {
        this.hintVoiceConfigList = hintVoiceConfigList;
    }
}
