package com.jd.bluedragon.common.dto.feedback;

import java.io.Serializable;

public class JyUserInfoDto implements Serializable {
    private static final long serialVersionUID = -4866999816006211921L;
    private Long appId;
    private String userAccount;
    private String userName;
    private Integer orgType;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }
}
