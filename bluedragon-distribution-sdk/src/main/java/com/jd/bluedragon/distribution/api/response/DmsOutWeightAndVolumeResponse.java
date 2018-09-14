package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class DmsOutWeightAndVolumeResponse  extends JdResponse {
    /**
     * 箱号/包裹号
     */
    private String barCode;
    /**
     * 操作站点
     */
    private Integer siteCode;
    /**
     * 重量
     */
    private Double weight;
    /**
     * 体积
     */
    private Double volume;
    /**
     * 称重人编码
     */
    private Integer weightUserCode;
    /**
     * 称重人姓名
     */
    private String weightUserName;
    /**
     * 量方人编码
     */
    private Integer measureUserCode;
    /**
     * 量方人姓名
     */
    private String measureUserName;

    public DmsOutWeightAndVolumeResponse(Integer code,String message){
        super(code,message);
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getWeightUserCode() {
        return weightUserCode;
    }

    public void setWeightUserCode(Integer weightUserCode) {
        this.weightUserCode = weightUserCode;
    }

    public String getWeightUserName() {
        return weightUserName;
    }

    public void setWeightUserName(String weightUserName) {
        this.weightUserName = weightUserName;
    }

    public Integer getMeasureUserCode() {
        return measureUserCode;
    }

    public void setMeasureUserCode(Integer measureUserCode) {
        this.measureUserCode = measureUserCode;
    }

    public String getMeasureUserName() {
        return measureUserName;
    }

    public void setMeasureUserName(String measureUserName) {
        this.measureUserName = measureUserName;
    }
}
