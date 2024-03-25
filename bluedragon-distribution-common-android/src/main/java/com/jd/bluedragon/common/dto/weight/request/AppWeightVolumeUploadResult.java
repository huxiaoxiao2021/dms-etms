package com.jd.bluedragon.common.dto.weight.request;

import java.io.Serializable;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-03-25 14:26
 **/
public class AppWeightVolumeUploadResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 校验结果
     */
    private Boolean checkResult;
    /**
     * 超重标识
     */
    private Boolean overLengthAndWeightFlag;
    /**
     * 需要确认标识
     */
    private Boolean needConfirm;
    /**
     * 是否已有-超重信息
     */
    private Boolean hasOverLengthAndWeight = Boolean.FALSE;

    //========================================================

    public Boolean getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Boolean checkResult) {
        this.checkResult = checkResult;
    }

    public Boolean getOverLengthAndWeightFlag() {
        return overLengthAndWeightFlag;
    }

    public void setOverLengthAndWeightFlag(Boolean overLengthAndWeightFlag) {
        this.overLengthAndWeightFlag = overLengthAndWeightFlag;
    }

    public Boolean getNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
    }

    public Boolean getHasOverLengthAndWeight() {
        return hasOverLengthAndWeight;
    }

    public void setHasOverLengthAndWeight(Boolean hasOverLengthAndWeight) {
        this.hasOverLengthAndWeight = hasOverLengthAndWeight;
    }
}
