package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;

public class RemindTransJobRequest implements Serializable {
    private static final long serialVersionUID = 6740683763062721890L;

    /**
     * 始发网点
     */
    private Integer sourceSiteCode;

    /**
     * 待派车任务号
     */
    private String transJobCode;

    /**
     * 运力资源编码
     */
    private String transportCode;

    /**
     * 用户erp
     */
    private String userErp;

    /**
     * 用户姓名
     */
    private String userName;

    public Integer getSourceSiteCode() {
        return sourceSiteCode;
    }

    public void setSourceSiteCode(Integer sourceSiteCode) {
        this.sourceSiteCode = sourceSiteCode;
    }

    public String getTransJobCode() {
        return transJobCode;
    }

    public void setTransJobCode(String transJobCode) {
        this.transJobCode = transJobCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
