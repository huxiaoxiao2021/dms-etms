package com.jd.bluedragon.common.dto.spotcheck;

import java.io.Serializable;

/**
 * 人工抽检结果对象
 *
 * @author hujiping
 * @date 2021/8/20 11:20 上午
 */
public class ArtificialSpotCheckResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否多包裹
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
