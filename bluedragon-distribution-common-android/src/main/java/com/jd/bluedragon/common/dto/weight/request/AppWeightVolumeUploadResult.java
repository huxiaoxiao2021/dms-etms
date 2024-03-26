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

    //========================================================

    public Boolean getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Boolean checkResult) {
        this.checkResult = checkResult;
    }
}
