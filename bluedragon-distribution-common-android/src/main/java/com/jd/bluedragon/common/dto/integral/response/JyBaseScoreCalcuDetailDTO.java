package com.jd.bluedragon.common.dto.integral.response;



import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/14
 */
public class JyBaseScoreCalcuDetailDTO {
    /*
     * 岗位类型
     */
    private Integer positionType;

    /*
     * 岗位类型
     */
    private String positionTypeName;

    /*
     * 操作量
     */
    private BigDecimal quantity;

    /*
     * 分数
     */
    private BigDecimal score;

    /*
     * 下一阶段差值
     */
    private BigDecimal toNextQuantity;

    /*
     * 下一阶段的分数
     */
    private BigDecimal nextScore;

    /*
     * 指标类型
     */
    private String quotaNo;

    /*
     * 指标名字
     */
    private String quotaName;

    /*
     * 操作量转分数规则列表
     */
    private List<JyBaseScoreRuleDTO> ruleDTOList;

    public Integer getPositionType() {
        return positionType;
    }

    public void setPositionType(Integer positionType) {
        this.positionType = positionType;
    }

    public String getPositionTypeName() {
        return positionTypeName;
    }

    public void setPositionTypeName(String positionTypeName) {
        this.positionTypeName = positionTypeName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getToNextQuantity() {
        return toNextQuantity;
    }

    public void setToNextQuantity(BigDecimal toNextQuantity) {
        this.toNextQuantity = toNextQuantity;
    }

    public BigDecimal getNextScore() {
        return nextScore;
    }

    public void setNextScore(BigDecimal nextScore) {
        this.nextScore = nextScore;
    }

    public String getQuotaNo() {
        return quotaNo;
    }

    public void setQuotaNo(String quotaNo) {
        this.quotaNo = quotaNo;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
    }

    public List<JyBaseScoreRuleDTO> getRuleDTOList() {
        return ruleDTOList;
    }

    public void setRuleDTOList(List<JyBaseScoreRuleDTO> ruleDTOList) {
        this.ruleDTOList = ruleDTOList;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
