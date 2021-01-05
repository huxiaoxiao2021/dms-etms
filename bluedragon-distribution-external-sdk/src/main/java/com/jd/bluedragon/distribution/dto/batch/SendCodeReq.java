package com.jd.bluedragon.distribution.dto.batch;

import java.io.Serializable;

/**
 * @ClassName SendCodeReq
 * @Description 创建批次请求体
 * @Author wyh
 * @Date 2021/1/4 17:04
 **/
public class SendCodeReq implements Serializable {

    private static final long serialVersionUID = -2246307391020398715L;

    /**
     * 请求系统来源
     */
    private Integer sysSource;

    /**
     * 批次始发地
     */
    private Integer beginningSiteCode;

    /**
     * 批次目的地
     */
    private Integer destSiteCode;

    /**
     * 创建人ERP
     */
    private String createUserErp;

    public Integer getSysSource() {
        return sysSource;
    }

    public void setSysSource(Integer sysSource) {
        this.sysSource = sysSource;
    }

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
}
