package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

/**
 * @Author: liming522
 * @Description: 标准对象
 * @Date: create in 2021/1/8 16:05
 */
public class StandardDto {

    /**
     * 是否超标
     * true 超标  false 未超标
     */
    private Boolean excessFlag;

    /**
     * 误差标准值
     */
    private String hitMessage;

    /**
     * 警告信息
     */
    private String warnMessage;

    /**
     * 超标原因
     */
    private String excessReason;

    public String getHitMessage() {
        return hitMessage;
    }

    public void setHitMessage(String hitMessage) {
        this.hitMessage = hitMessage;
    }

    public Boolean getExcessFlag() {
        return excessFlag;
    }

    public void setExcessFlag(Boolean excessFlag) {
        this.excessFlag = excessFlag;
    }

    public String getWarnMessage() {
        return warnMessage;
    }

    public void setWarnMessage(String warnMessage) {
        this.warnMessage = warnMessage;
    }

    public String getExcessReason() {
        return excessReason;
    }

    public void setExcessReason(String excessReason) {
        this.excessReason = excessReason;
    }
}
    
