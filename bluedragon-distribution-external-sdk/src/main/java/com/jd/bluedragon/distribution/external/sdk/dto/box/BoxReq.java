package com.jd.bluedragon.distribution.external.sdk.dto.box;

import java.io.Serializable;
import java.util.Date;

public class BoxReq implements Serializable {

    private String boxCode;//箱号

    private Integer boxStatus;//箱状态

    private Integer opSiteCode;//操作箱站点编号

    private String opSiteName;//操作箱站点名

    private Date opTime;//操作时间

    private String opErp;//操作人erp

    private Integer opNodeCode;//操作节点编号

    private String opNodeName;//操作节点名

    private String opDescription;//操作描述


    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(Integer boxStatus) {
        this.boxStatus = boxStatus;
    }

    public Integer getOpSiteCode() {
        return opSiteCode;
    }

    public void setOpSiteCode(Integer opSiteCode) {
        this.opSiteCode = opSiteCode;
    }

    public String getOpSiteName() {
        return opSiteName;
    }

    public void setOpSiteName(String opSiteName) {
        this.opSiteName = opSiteName;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public String getOpErp() {
        return opErp;
    }

    public void setOpErp(String opErp) {
        this.opErp = opErp;
    }

    public Integer getOpNodeCode() {
        return opNodeCode;
    }

    public void setOpNodeCode(Integer opNodeCode) {
        this.opNodeCode = opNodeCode;
    }

    public String getOpNodeName() {
        return opNodeName;
    }

    public void setOpNodeName(String opNodeName) {
        this.opNodeName = opNodeName;
    }

    public String getOpDescription() {
        return opDescription;
    }

    public void setOpDescription(String opDescription) {
        this.opDescription = opDescription;
    }
}
