package com.jd.bluedragon.common.dto.sendcode.request;

import java.io.Serializable;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.common.dto.sendcode.request
 * @Description:
 * @date Date : 2023年06月15日 13:58
 */
public class SendCodeGenRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 起始场地
     */
    private Integer createSiteCode;
    /**
     * 目的场地
     */
    private Integer receiveSiteCode;
    /**
     * 操作人
     */
    private String createUserErp;

    //======================================

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

}
