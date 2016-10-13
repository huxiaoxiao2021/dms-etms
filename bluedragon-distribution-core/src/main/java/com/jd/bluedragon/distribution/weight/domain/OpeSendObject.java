package com.jd.bluedragon.distribution.weight.domain;

/**
 * Created by lixin39 on 2016/10/11.
 */
public class OpeSendObject {

    /// <summary>
    /// 包裹号
    /// </summary>
    private String package_code;

    /// <summary>
    /// 包裹重量	单位（KG）
    /// </summary>
    private Float weight;

    /// <summary>
    /// 体积 	单位（立方厘米）
    /// </summary>
    private Float volume;

    /// <summary>
    /// 操作站点ID
    /// </summary>
    private int dms_site_id;

    /// <summary>
    /// 操作时间
    /// </summary>
    private Long thisUpdateTime;

    public String getPackage_code() {
        return package_code;
    }

    public void setPackage_code(String package_code) {
        this.package_code = package_code;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public int getDms_site_id() {
        return dms_site_id;
    }

    public void setDms_site_id(int dms_site_id) {
        this.dms_site_id = dms_site_id;
    }

    public Long getThisUpdateTime() {
        return thisUpdateTime;
    }

    public void setThisUpdateTime(Long thisUpdateTime) {
        this.thisUpdateTime = thisUpdateTime;
    }

    @Override
    public String toString() {
        return "OpeSendObject{" +
                "package_code='" + package_code + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", dms_site_id=" + dms_site_id +
                ", thisUpdateTime=" + thisUpdateTime +
                '}';
    }

}
