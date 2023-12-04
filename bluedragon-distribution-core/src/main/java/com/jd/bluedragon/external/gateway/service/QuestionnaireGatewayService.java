package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.questionnaire.AnswerQuestionnaireReq;
import com.jd.bluedragon.common.dto.questionnaire.QuestionnaireReq;

import java.util.List;

/**
 * 调查问卷服务
 */
public interface QuestionnaireGatewayService {


    /**
     * 获取问卷
     *
     * @param req 问卷请求对象
     * @return 响应对象，包含问卷列表
     */
    JdCResponse<String> getQuestionnaire(QuestionnaireReq req);


    /**
     * 提交问卷答案
     *
     * @param req 提交问卷答案请求对象
     * @return
     */
    JdCResponse<Void> answerQuestionnaire(AnswerQuestionnaireReq req);

}
