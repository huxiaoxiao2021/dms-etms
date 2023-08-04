package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 16:52
 * @Description
 */
public class ScanAndCheckTransportInfoReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 运力编码
     */
    private String transportCode;

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
