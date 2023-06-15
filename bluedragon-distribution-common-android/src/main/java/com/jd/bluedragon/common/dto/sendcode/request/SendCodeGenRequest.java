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
    private Integer beginningSiteCode;
    /**
     * 目的场地
     */
    private Integer destSiteCode;
    /**
     * 操作人
     */
    private String createUserErp;
    /**
     * 来源
     */
    private Integer sysSource;

    //======================================


    public Integer getBeginningSiteCode() {
        return beginningSiteCode;
    }

    public void setBeginningSiteCode(Integer beginningSiteCode) {
        this.beginningSiteCode = beginningSiteCode;
    }

    public Integer getDestSiteCode() {
        return destSiteCode;
    }

    public void setDestSiteCode(Integer destSiteCode) {
        this.destSiteCode = destSiteCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public Integer getSysSource() {
        return sysSource;
    }

    public void setSysSource(Integer sysSource) {
        this.sysSource = sysSource;
    }
}
