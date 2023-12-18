package com.jd.bluedragon.common.dto.questionnaire;

import java.io.Serializable;
import java.util.List;

/**
 * 答案
 */
public class AnswerItem implements Serializable {

    /**
     * 问题id
     */
    private String questionId;

    /**
     * 问题类型
     */
    private String type;

    /**
     * 选项数据
     */
    private List<AnswerOptionItem> options;

    /**
     * 矩阵题答案信息
     */
    private List<AnswerGroupItem> groups;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AnswerOptionItem> getOptions() {
        return options;
    }

    public void setOptions(List<AnswerOptionItem> options) {
        this.options = options;
    }

    public List<AnswerGroupItem> getGroups() {
        return groups;
    }

    public void setGroups(List<AnswerGroupItem> groups) {
        this.groups = groups;
    }
}
