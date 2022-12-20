package com.jd.bluedragon.common.dto.integral.response;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/14
 */
public class JyIntegralDetailDTO {

    private Date operateDate;

    private Long orgCode;

    private Long siteCode;

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public Long getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Long orgCode) {
        this.orgCode = orgCode;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getJobCode() {
        return jobCode;
    }

    public void setJobCode(Integer jobCode) {
        this.jobCode = jobCode;
    }

    public BigDecimal getAverageCoefficient() {
        return averageCoefficient;
    }

    public void setAverageCoefficient(BigDecimal averageCoefficient) {
        this.averageCoefficient = averageCoefficient;
    }

    public BigDecimal getIntegral() {
        return integral;
    }

    public void setIntegral(BigDecimal integral) {
        this.integral = integral;
    }

    public BigDecimal getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(BigDecimal totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public List<JyBaseScoreCalcuDetailDTO> getCalcuDetailDTOList() {
        return calcuDetailDTOList;
    }

    public void setCalcuDetailDTOList(List<JyBaseScoreCalcuDetailDTO> calcuDetailDTOList) {
        this.calcuDetailDTOList = calcuDetailDTOList;
    }

    public List<JyIntegralCoefficientDetailDTO> getIntegralCoefficientDetailDTOList() {
        return integralCoefficientDetailDTOList;
    }

    public void setIntegralCoefficientDetailDTOList(List<JyIntegralCoefficientDetailDTO> integralCoefficientDetailDTOList) {
        this.integralCoefficientDetailDTOList = integralCoefficientDetailDTOList;
    }

    /*
     * ERP或身份证
     */
    private String userCode;

    /*
     * 工种
     */
    private Integer jobCode;

    /*
     * 平均系数
     */
    private BigDecimal averageCoefficient;

    /*
     * 积分/今日积分
     */
    private BigDecimal integral;

    /*
     * 总积分
     */
    private BigDecimal totalIntegral;

    /*
     * 基础得分
     */
    private BigDecimal totalScore;

    /*
     * 基础分数明细
     */
    private List<JyBaseScoreCalcuDetailDTO> calcuDetailDTOList;

    /*
     * 积分系数明细
     */
    private List<JyIntegralCoefficientDetailDTO> integralCoefficientDetailDTOList;
}
