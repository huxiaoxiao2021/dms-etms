package com.jd.bluedragon.distribution.waybill.domain;

import java.util.Date;

/**
 * Created by zhanglei51 on 2016/12/15.
 * 运单包裹重量体积domain
 */
public class WaybillPackageDTO implements java.io.Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 2292780849123455632L;

    //id
    private Long id;

    //运单号
    private String waybillCode;

    //包裹号
    private String packageCode;

    //原始重量（仓库打包）
    private Double originalWeight;

    //复重(分拣称重)
    private Double weight;

    //长度
    private Double length;

    //宽度
    private Double width;

    //高度
    private Double height;

    //原始体积
    private Double originalVolume;

    //分拣量方体积
    private Double volume;

    //称重/量方人
    private String createUserCode;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Double getOriginalWeight() {
        return originalWeight;
    }

    public void setOriginalWeight(Double originalWeight) {
        this.originalWeight = originalWeight;
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

    public Double getOriginalVolume() {
        return originalVolume;
    }

    public void setOriginalVolume(Double originalVolume) {
        this.originalVolume = originalVolume;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
