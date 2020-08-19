package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @ClassName ThirdWaybillRequest
 * @Description
 * @Author wyh
 * @Date 2020/7/20 14:48
 **/
public class ThirdWaybillRequest extends JdRequest {

    private static final long serialVersionUID = -7634181219547436335L;

    /**
     * erp
     */
    private String userErp;

    /**
     * 三方运单号
     */
    private String thirdWaybillCode;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getThirdWaybillCode() {
        return thirdWaybillCode;
    }

    public void setThirdWaybillCode(String thirdWaybillCode) {
        this.thirdWaybillCode = thirdWaybillCode;
    }
}
