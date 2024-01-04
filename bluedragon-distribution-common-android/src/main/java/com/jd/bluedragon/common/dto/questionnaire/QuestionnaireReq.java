package com.jd.bluedragon.common.dto.questionnaire;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * 调查问卷查询
 */
public class QuestionnaireReq implements Serializable {

    private String userErp;

    private String userName;

    private String positionCode;

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

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }
}
