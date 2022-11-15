package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class CreateGroupCTTResp implements Serializable {
    private static final long serialVersionUID = 4564343481822610384L;
    private String templateCode;
    private String templateName;

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
}
