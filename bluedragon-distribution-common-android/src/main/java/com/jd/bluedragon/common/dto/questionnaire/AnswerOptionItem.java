package com.jd.bluedragon.common.dto.questionnaire;

import java.io.Serializable;

public class AnswerOptionItem implements Serializable {

    /**
     * 选项id
     */
    private String optionId;

    /**
     * 选项标题
     */
    private String name;

    /**
     *  填空内容
     */
    private String text;

    /**
     * 附加填空内容
     */
    private String extraText;

    /**
     *  选项得分
     */
    private Integer score;

    /**
     * 选项排序
     */
    private Integer order;

    /**
     * 父节点id
     */
    private String parentId;

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
