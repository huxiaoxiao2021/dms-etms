package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/31 10:32
 * @Description:
 */
public class ExpSignUserResp implements Serializable {

    /**
     * 异常签到用户erp
     */
    private String expUserErp;

    /**
     * 异常签到用户id
     */
    private String expUserCode;

    /**
     * 异常签到用户名称
     */
    private String expUserName;


    public String getExpUserErp() {
        return expUserErp;
    }

    public void setExpUserErp(String expUserErp) {
        this.expUserErp = expUserErp;
    }

    public String getExpUserCode() {
        return expUserCode;
    }

    public void setExpUserCode(String expUserCode) {
        this.expUserCode = expUserCode;
    }

    public String getExpUserName() {
        return expUserName;
    }

    public void setExpUserName(String expUserName) {
        this.expUserName = expUserName;
    }
}
