package com.jd.bluedragon.common.dto.jyexpection.request;

import java.math.BigDecimal;

/**
 * 异常任务处理提交提交重量体积入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-17 18:08:58 周三
 */
public class ExpInterceptTaskProcessSubmitWeightVolumeReq extends ExpTaskCommonReq {
    private static final long serialVersionUID = -6302943955143049743L;

    /**
     * 保存类型，0-暂存，1-提交
     */
    private Integer saveType;

    /**
     * 长，单位cm
     */
    private BigDecimal length;

    /**
     * 宽，单位cm
     */
    private BigDecimal width;

    /**
     * 高，单位cm
     */
    private BigDecimal height;

    /**
     * 重量，单位kg
     */
    private BigDecimal weight;

    public ExpInterceptTaskProcessSubmitWeightVolumeReq() {
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
