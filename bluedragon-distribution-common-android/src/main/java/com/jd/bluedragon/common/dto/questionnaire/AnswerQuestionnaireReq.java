package com.jd.bluedragon.common.dto.questionnaire;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class AnswerQuestionnaireReq extends BaseReq implements Serializable {

    private AnswerQuestionnaire answerQuestionnaire;

    public AnswerQuestionnaire getAnswerQuestionnaire() {
        return answerQuestionnaire;
    }

    public void setAnswerQuestionnaire(AnswerQuestionnaire answerQuestionnaire) {
        this.answerQuestionnaire = answerQuestionnaire;
    }
}
