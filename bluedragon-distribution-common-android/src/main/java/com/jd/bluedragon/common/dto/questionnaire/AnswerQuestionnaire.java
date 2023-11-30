package com.jd.bluedragon.common.dto.questionnaire;

import java.io.Serializable;
import java.util.List;

public class AnswerQuestionnaire implements Serializable {

    /**
     * 问卷id
     */
    private String questionnaireId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 第一道题答题的时刻
     */
    private Long firstQuestionTime;

    /**
     * 问卷版本（暂定传1）
     */
    private Integer questionnaireVersion;

    /**
     * 渠道信息
     */
    private String source;

    /**
     * 是否提前结束
     */
    private String isEarlyEnd;

    /**
     * 额外参数1
     */

    private String extra1;
    /**
     * 额外参数2
     */
    private String extra2;
    /**
     * 额外参数3
     */
    private String extra3;
    /**
     * 额外参数4
     */
    private String extra4;
    /**
     * 额外参数5
     */
    private String extra5;

    /**
     * 答案
     */
    private List<AnswerItem> answers;

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getFirstQuestionTime() {
        return firstQuestionTime;
    }

    public void setFirstQuestionTime(Long firstQuestionTime) {
        this.firstQuestionTime = firstQuestionTime;
    }

    public Integer getQuestionnaireVersion() {
        return questionnaireVersion;
    }

    public void setQuestionnaireVersion(Integer questionnaireVersion) {
        this.questionnaireVersion = questionnaireVersion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIsEarlyEnd() {
        return isEarlyEnd;
    }

    public void setIsEarlyEnd(String isEarlyEnd) {
        this.isEarlyEnd = isEarlyEnd;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    public String getExtra4() {
        return extra4;
    }

    public void setExtra4(String extra4) {
        this.extra4 = extra4;
    }

    public String getExtra5() {
        return extra5;
    }

    public void setExtra5(String extra5) {
        this.extra5 = extra5;
    }

    public List<AnswerItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerItem> answers) {
        this.answers = answers;
    }
}
