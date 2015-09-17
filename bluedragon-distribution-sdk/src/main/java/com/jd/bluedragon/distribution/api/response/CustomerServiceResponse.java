package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class CustomerServiceResponse extends JdResponse {

    private static final long serialVersionUID = 6421643159029953636L;

    public static final Integer CODE_NEW_BILL_CODE_NOT_FOUND = 20201;
    public static final String MESSAGE_NEW_BILL_CODE_NOT_FOUND = "未检索到新运单号";

    /**
     * 新单号
     */
    private String surfaceCode;

    private String serviceCode;


    public CustomerServiceResponse() {
        super();
    }

    public CustomerServiceResponse(Integer code, String message) {
        super(code, message);
    }

    public String getSurfaceCode() {
        return this.surfaceCode;
    }

    public void setSurfaceCode(String surfaceCode) {
        this.surfaceCode = surfaceCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
