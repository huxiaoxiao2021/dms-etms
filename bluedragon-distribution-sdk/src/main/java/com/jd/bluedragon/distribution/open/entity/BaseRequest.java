package com.jd.bluedragon.distribution.open.entity;

import java.io.Serializable;

/**
 * 基础接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-13 14:58:55 周五
 */
public class BaseRequest implements Serializable {

    private static final long serialVersionUID = 5402735178994361066L;

    /**
     * 调用信息
     */
    private RequestProfile requestProfile;

    public BaseRequest() {
    }

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }
}
