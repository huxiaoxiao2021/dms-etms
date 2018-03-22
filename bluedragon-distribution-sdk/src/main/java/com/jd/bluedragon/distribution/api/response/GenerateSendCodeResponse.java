package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * Created by jinjingcheng on 2018/3/7.
 */
public class GenerateSendCodeResponse extends JdResponse{

    private static final long serialVersionUID = 4678867119796539126L;
    private String sendCode;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
