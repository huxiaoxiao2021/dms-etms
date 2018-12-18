package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年12月18日 16时:35分
 */
public class ClientRequest  extends JdRequest {
    /**
     * 业务编码
     */
    private String businessCode;
    /**
     * 业务编码
     */
    private String businessName;

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }
}
