package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class CreateGroupCTTResp implements Serializable {
    private static final long serialVersionUID = 4564343481822610384L;
    private String templateCode;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
}
