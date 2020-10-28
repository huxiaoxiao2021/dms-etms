package com.jd.bluedragon.external.gateway.dto.request;

import java.io.Serializable;

/**
 * 推送众邮箱号 MQ 消息体
 */
public class ThirdBoxCodeMessageVO implements Serializable {
    private static final long serialVersionUID = -8337774316064080887L;
    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 始发站点编号
     */
    private String createSiteCode;
    /**
     * 接收站点编号
     */
    private String receiveSiteCode;

    /**
     * 箱号类型
     */
    private String boxType;

    /**
     * 始发站点类型
     */
    private String createSiteType;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(String createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(String receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getCreateSiteType() {
        return createSiteType;
    }

    public void setCreateSiteType(String createSiteType) {
        this.createSiteType = createSiteType;
    }

    @Override
    public String toString() {
        return "ThirdBoxCodeMessageVO{" +
                "boxCode='" + boxCode + '\'' +
                ", createSiteCode='" + createSiteCode + '\'' +
                ", receiveSiteCode='" + receiveSiteCode + '\'' +
                ", boxType='" + boxType + '\'' +
                ", createSiteType='" + createSiteType + '\'' +
                '}';
    }
}
