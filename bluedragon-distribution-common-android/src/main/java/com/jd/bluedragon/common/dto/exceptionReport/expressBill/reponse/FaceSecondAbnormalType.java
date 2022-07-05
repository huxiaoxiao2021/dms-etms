package com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse;

import java.io.Serializable;

/**
 * 面单举报二级原因
 *
 * @author hujiping
 * @date 2022/5/31 4:19 PM
 */
public class FaceSecondAbnormalType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer abnormalCode;
    private String abnormalName;

    public Integer getAbnormalCode() {
        return abnormalCode;
    }

    public void setAbnormalCode(Integer abnormalCode) {
        this.abnormalCode = abnormalCode;
    }

    public String getAbnormalName() {
        return abnormalName;
    }

    public void setAbnormalName(String abnormalName) {
        this.abnormalName = abnormalName;
    }

    @Override
    public String toString() {
        return getAbnormalName() == null ? "" : getAbnormalName();
    }
}
