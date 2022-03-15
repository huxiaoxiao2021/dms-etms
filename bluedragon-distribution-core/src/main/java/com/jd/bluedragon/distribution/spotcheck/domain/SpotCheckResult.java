package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;

/**
 * 抽检记过
 */
public class SpotCheckResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 超标状态
     */
    private Boolean isMultiPack = false;
    /**
     * 超标状态
     */
    private Integer excessStatus;

    /**
     * 超标类型
     */
    private Integer excessType;

    public Boolean getIsMultiPack() {
        return isMultiPack;
    }

    public void setIsMultiPack(Boolean multiPack) {
        isMultiPack = multiPack;
    }

    public Integer getExcessStatus() {
        return excessStatus;
    }

    public void setExcessStatus(Integer excessStatus) {
        this.excessStatus = excessStatus;
    }

    public Integer getExcessType() {
        return excessType;
    }

    public void setExcessType(Integer excessType) {
        this.excessType = excessType;
    }
}
