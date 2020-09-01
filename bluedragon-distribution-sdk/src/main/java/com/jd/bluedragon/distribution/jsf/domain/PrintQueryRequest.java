package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;

/**
 * 打印面单请求DTO
 * <p>
 *     目前用于查询集包地
 * </p>
 *
 * @author: hujiping
 * @date: 2020/8/27 20:54
 */
public class PrintQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 始发网点编码
     */
    private Integer originalDmsCode;

    /**
     * 始发网点名称
     */
    private String  originalDmsName;

    /**
     * 目的网点编码
     */
    private Integer destinationDmsCode;

    /**
     * 目的网点名称
     */
    private String destinationDmsName;

    /**
     * 包裹重量
     * */
    private Double packageWeight;

    /**
     * 运输类型
     *  1、航空运输 2、公路运输
     * */
    private Integer transportType;

    /**
     * 规则类型
     *  1、建包
     * */
    private Integer ruleType;

    public Integer getOriginalDmsCode() {
        return originalDmsCode;
    }

    public void setOriginalDmsCode(Integer originalDmsCode) {
        this.originalDmsCode = originalDmsCode;
    }

    public String getOriginalDmsName() {
        return originalDmsName;
    }

    public void setOriginalDmsName(String originalDmsName) {
        this.originalDmsName = originalDmsName;
    }

    public Integer getDestinationDmsCode() {
        return destinationDmsCode;
    }

    public void setDestinationDmsCode(Integer destinationDmsCode) {
        this.destinationDmsCode = destinationDmsCode;
    }

    public String getDestinationDmsName() {
        return destinationDmsName;
    }

    public void setDestinationDmsName(String destinationDmsName) {
        this.destinationDmsName = destinationDmsName;
    }

    public Double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(Double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }
}
