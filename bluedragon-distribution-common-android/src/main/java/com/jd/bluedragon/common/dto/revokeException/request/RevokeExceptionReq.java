package com.jd.bluedragon.common.dto.revokeException.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

/**
 * @author liwenji
 * @description 撤销异常提报
 * @date 2023-01-04 14:21
 */
public class RevokeExceptionReq extends BaseReq {

    /**
     * 异常编号
     */
    private String transAbnormalCode;

    public String getTransAbnormalCode() {
        return transAbnormalCode;
    }

    public void setTransAbnormalCode(String transAbnormalCode) {
        this.transAbnormalCode = transAbnormalCode;
    }
}
