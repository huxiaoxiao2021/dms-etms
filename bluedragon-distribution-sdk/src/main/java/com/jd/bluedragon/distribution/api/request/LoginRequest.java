package com.jd.bluedragon.distribution.api.request;

/**
 * 登录request对象
 * Created by shipeilin on 2018/1/16.
 */
public class LoginRequest extends BaseRequest{
    private static final long serialVersionUID = 3477068159073538416L;

    /** 主机名称 */
    private String clientInfo;

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }
}
