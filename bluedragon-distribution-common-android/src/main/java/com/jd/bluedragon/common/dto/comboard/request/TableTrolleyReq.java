package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class TableTrolleyReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 2913336393003471073L;
    /**
     * 滑道编号
     */
    private String crossCode;

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }
}
