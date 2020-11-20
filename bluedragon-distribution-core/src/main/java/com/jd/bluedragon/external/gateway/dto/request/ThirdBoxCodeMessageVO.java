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

    @Override
    public String toString() {
        return "ThirdBoxCodeMessageVO{" +
                "boxCode='" + boxCode + '\'' +
                ", createSiteCode='" + createSiteCode + '\'' +
                ", receiveSiteCode='" + receiveSiteCode + '\'' +
                '}';
    }
}
