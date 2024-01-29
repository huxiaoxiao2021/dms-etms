package com.jd.bluedragon.common.dto.spotcheck;

import java.io.Serializable;

/**
 * AI图片识别请求
 *
 * @author hujiping
 * @date 2021/12/27 3:05 PM
 */
public class PicAutoDistinguishRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 超标类型
     */
    private Integer excessType;

    /**
     * 图片类型 @see SpotCheckPicTypeEnum
     */
    private Integer picType;

    /**
     * 图片链接
     */
    private String picUrl;

    /**
     * 版本：值为1时代表从新版人工抽检(支持视频)进来
     */
    private Integer version;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getExcessType() {
        return excessType;
    }

    public void setExcessType(Integer excessType) {
        this.excessType = excessType;
    }

    public Integer getPicType() {
        return picType;
    }

    public void setPicType(Integer picType) {
        this.picType = picType;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
