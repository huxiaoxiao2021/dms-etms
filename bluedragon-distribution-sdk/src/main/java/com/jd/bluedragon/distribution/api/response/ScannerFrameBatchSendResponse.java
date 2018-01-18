package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class ScannerFrameBatchSendResponse extends JdResponse {

    public ScannerFrameBatchSendResponse(Integer code, String message){
        super(code, message);
    }

    private String sendCode;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
