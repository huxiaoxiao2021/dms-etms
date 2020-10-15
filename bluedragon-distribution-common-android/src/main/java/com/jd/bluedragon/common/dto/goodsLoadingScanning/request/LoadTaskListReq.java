package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 装车任务列表
 * @author: wuming
 * @create: 2020-10-14 17:24
 */
public class LoadTaskListReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 操作人ERP */
    private String loginUserErp;

    /** 操作人名字 */
    private String loginUserName;

    public LoadTaskListReq() {
    }

    public String getLoginUserErp() {
        return loginUserErp;
    }

    public void setLoginUserErp(String loginUserErp) {
        this.loginUserErp = loginUserErp;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }
}
