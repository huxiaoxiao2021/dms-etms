package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class ThirdWaybillNoRequest extends JdRequest {


    private static final long serialVersionUID = -6222572140929074706L;
    /**
     * erp
     */
    private String userErp;

    /**
     * 运单号
     */
    private String thirdWaybill;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getThirdWaybill() {
        return thirdWaybill;
    }

    public void setThirdWaybill(String thirdWaybill) {
        this.thirdWaybill = thirdWaybill;
    }

    @Override
    public String toString() {
        return "ThirdWaybillNoRequest{" +
                "userErp='" + userErp + '\'' +
                ", thirdWaybill='" + thirdWaybill + '\'' +
                '}';
    }
}
