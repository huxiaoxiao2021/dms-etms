package com.jd.bluedragon.distribution.weightVolume.domain;

/**
 * 称重量方规则参数
 *
 * @author hujiping
 * @date 2021/3/26 4:01 下午
 */
public class WeightVolumeRuleCheckDto {

    /**
     * 操作单号
     */
    private String barCode;

    /**
     * 处理类型
     * @see com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum
     */
    private String businessType;

    /**
     * 业务来源
     * @see com.jd.bluedragon.distribution.weightvolume.FromSourceEnum
     */
    private String sourceCode;

    /**
     * 重量 单位KG
     */
    private Double weight;

    /**
     * 长度 单位CM
     */
    private Double length;

    /**
     * 宽 单位CM
     */
    private Double width;

    /**
     * 高 单位CM
     */
    private Double height;

    /**
     * 体积 单位cm³
     */
    private Double volume;

    /**
     * 是否开启称重
     */
    private Boolean checkWeight;

    /**
     * 是否开启长宽高校验
     */
    private Boolean checkLWH;
    /**
     * 是否开启体积校验
     */
    private Boolean checkVolume;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Boolean getCheckWeight() {
        return checkWeight;
    }

    public void setCheckWeight(Boolean checkWeight) {
        this.checkWeight = checkWeight;
    }

    public Boolean getCheckLWH() {
        return checkLWH;
    }

    public void setCheckLWH(Boolean checkLWH) {
        this.checkLWH = checkLWH;
    }

    public Boolean getCheckVolume() {
        return checkVolume;
    }

    public void setCheckVolume(Boolean checkVolume) {
        this.checkVolume = checkVolume;
    }
}
