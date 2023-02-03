package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class GetSendRouterInfoReq extends JyReqBaseDto implements Serializable {
    private static final long serialVersionUID = -6213986344603959778L;
    private String scanCode;

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }
}
