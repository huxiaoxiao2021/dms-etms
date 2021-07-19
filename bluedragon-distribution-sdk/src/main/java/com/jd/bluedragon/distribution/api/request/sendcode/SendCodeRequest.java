package com.jd.bluedragon.distribution.api.request.sendcode;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @ClassName SendCodeRequest
 * @Description
 * @Author wyh
 * @Date 2021/6/28 19:00
 **/
public class SendCodeRequest extends JdRequest {

    private static final long serialVersionUID = 6882889269109371588L;

    /**
     * erp
     */
    private String userErp;

    /**
     * 批次号
     */
    private String sendCode;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
