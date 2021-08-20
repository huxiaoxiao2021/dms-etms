package com.jd.bluedragon.distribution.spotcheck.domain;

/**
 * 校验超标结果
 *
 * @author hujiping
 * @date 2021/8/24 6:10 下午
 */
public class CheckExcessResult {

    /**
     * 超标编码
     */
    private Integer excessCode;
    /**
     * 超标原因
     */
    private String excessReason;
    /**
     * 超标误差标准值
     */
    private String excessStandard;

    public Integer getExcessCode() {
        return excessCode;
    }

    public void setExcessCode(Integer excessCode) {
        this.excessCode = excessCode;
    }

    public String getExcessReason() {
        return excessReason;
    }

    public void setExcessReason(String excessReason) {
        this.excessReason = excessReason;
    }

    public String getExcessStandard() {
        return excessStandard;
    }

    public void setExcessStandard(String excessStandard) {
        this.excessStandard = excessStandard;
    }
}
