package com.jd.bluedragon.common.dto.questionnaire;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class AnswerQuestionnaireReq implements Serializable {

    private AnswerQuestionnaire answerQuestionnaire;

    private String userErp;

    public AnswerQuestionnaire getAnswerQuestionnaire() {
        return answerQuestionnaire;
    }

    public void setAnswerQuestionnaire(AnswerQuestionnaire answerQuestionnaire) {
        this.answerQuestionnaire = answerQuestionnaire;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
