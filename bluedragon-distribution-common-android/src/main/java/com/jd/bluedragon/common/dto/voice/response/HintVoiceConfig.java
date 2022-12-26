package com.jd.bluedragon.common.dto.voice.response;

import com.jd.bluedragon.common.dto.voice.enums.HintVoiceTypeEnum;

import java.io.Serializable;

/**
 * 提示音配置
 *
 * @author hujiping
 * @date 2022/10/26 5:42 PM
 */
public class HintVoiceConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 提示音编码
     */
    private String hintVoiceCode;

    /**
     * 提示音类型
     * @see HintVoiceTypeEnum
     */
    private Integer hintVoiceType;

    /**
     * 提示音下载链接
     */
    private String hintVoiceUrl;

    public String getHintVoiceCode() {
        return hintVoiceCode;
    }

    public void setHintVoiceCode(String hintVoiceCode) {
        this.hintVoiceCode = hintVoiceCode;
    }

    public Integer getHintVoiceType() {
        return hintVoiceType;
    }

    public void setHintVoiceType(Integer hintVoiceType) {
        this.hintVoiceType = hintVoiceType;
    }

    public String getHintVoiceUrl() {
        return hintVoiceUrl;
    }

    public void setHintVoiceUrl(String hintVoiceUrl) {
        this.hintVoiceUrl = hintVoiceUrl;
    }
}
