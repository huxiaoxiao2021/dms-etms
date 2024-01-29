package com.jd.bluedragon.distribution.api.request.box;

import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.base.RequestProfile;

import java.io.Serializable;

/**
 * 箱号类型请求入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-10-24 09:27:16 周二
 */
public class BoxTypeReq implements Serializable {

    private static final long serialVersionUID = 959618722450997046L;

    /**
     * 调用方信息
     */
    private RequestProfile requestProfile;

    /**
     * 操作人信息
     */
    private OperateUser operateUser;

    public BoxTypeReq() {
    }

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public BoxTypeReq setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
        return this;
    }
}
