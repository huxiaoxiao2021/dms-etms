package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request;

import java.io.Serializable;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-25 21:39:17 周二
 */
public class InspectionFinishSubmitRequest extends InspectionCommonRequest implements Serializable {
    private static final long serialVersionUID = -6144893268190899432L;

    /**
     * 异常标识
     */
    private Boolean abnormalFlag;

    public Boolean getAbnormalFlag() {
        return abnormalFlag;
    }

    public void setAbnormalFlag(Boolean abnormalFlag) {
        this.abnormalFlag = abnormalFlag;
    }

}
