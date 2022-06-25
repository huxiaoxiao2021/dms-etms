package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class GetSendRouterInfoReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6213986344603959778L;
    private String scanCode;

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }
}
