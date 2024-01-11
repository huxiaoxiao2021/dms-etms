package com.jd.bluedragon.distribution.api.request.box;


import com.jd.bluedragon.distribution.api.request.base.OperateUser;
import com.jd.bluedragon.distribution.api.request.base.RequestProfile;

import java.io.Serializable;

/**
 * 箱号打印入参
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public class BoxPrintReq implements Serializable {

    private static final long serialVersionUID = -8128040530625817445L;

    /**
     * 调用方信息
     */
    private RequestProfile requestProfile;

    /**
     * 操作人信息
     */
    private OperateUser operateUser;

    /**
     * 箱号
     */
    private String boxCode;

    public BoxPrintReq() {
    }

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public BoxPrintReq setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
        return this;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public BoxPrintReq setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
        return this;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public BoxPrintReq setBoxCode(String boxCode) {
        this.boxCode = boxCode;
        return this;
    }
}

