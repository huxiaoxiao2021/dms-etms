package com.jd.bluedragon.distribution.gantry.domain;

import java.io.Serializable;

/**
 * 龙门架驻厂扫描消息体
 *
 * @author: hujiping
 * @date: 2020/10/22 17:03
 */
public class GantryResidentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 箱号
     * */
    private String boxCode;

    /**
     * 扫描条码
     * */
    private String barCode;

    /**
     * 重量 单位KG
     * */
    private Double weight;

    /**
     * 体积 单位cm³
     * */
    private Double volume;

    /**
     * 长 单位CM
     * */
    private Double length;

    /**
     * 宽 单位CM
     * */
    private Double width;

    /**
     * 高 单位CM
     * */
    private Double height;

    /**
     * 寄托物名称
     * */
    private String consignGood;

    /**
     * 操作人青龙ID
     * */
    private Integer operatorId;

    /**
     * 操作人ERP
     * */
    private String operatorErp;

    /**
     * 操作人名称
     * */
    private String operatorName;

    /**
     * 操作场地ID
     * */
    private Integer operateSiteCode;

    /**
     * 操作场地名称
     * */
    private String operateSiteName;

    /**
     * 操作时间："yyyy-MM-dd HH:mm:ss"
     * */
    private String operateTime;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

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

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
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

    public String getConsignGood() {
        return consignGood;
    }

    public void setConsignGood(String consignGood) {
        this.consignGood = consignGood;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
