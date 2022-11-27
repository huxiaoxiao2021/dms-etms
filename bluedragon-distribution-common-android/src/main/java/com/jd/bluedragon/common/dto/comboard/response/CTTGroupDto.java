package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class CTTGroupDto implements Serializable {
    private static final long serialVersionUID = 4594229741391559526L;
    /**
     * 混扫任务编号
     */
    private String templateCode;
    /**
     * 混扫任务名称
     */
    private String templateName;
    /**
     * 创建任务erp
     */
    private String createUserErp;
    /**
     * 创建人名称
     */
    private String createUserName;
    /**
     * 流向数量
     */
    private Integer sendFlowCount;
    /**
     * 滑道号
     */
    private String crossCode;

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getSendFlowCount() {
        return sendFlowCount;
    }

    public void setSendFlowCount(Integer sendFlowCount) {
        this.sendFlowCount = sendFlowCount;
    }


}
