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
     * 站点编码
     */
    private Integer siteCode;

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

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
