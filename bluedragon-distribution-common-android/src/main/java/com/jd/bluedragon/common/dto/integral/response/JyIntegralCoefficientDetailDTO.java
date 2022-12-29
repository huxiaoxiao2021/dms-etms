package com.jd.bluedragon.common.dto.integral.response;


import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/14
 */
public class JyIntegralCoefficientDetailDTO {

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

    public List<JyQuotaCoefficientDetailDTO> getQuotaCoefficientDetailDTOS() {
        return quotaCoefficientDetailDTOS;
    }

    public void setQuotaCoefficientDetailDTOS(List<JyQuotaCoefficientDetailDTO> quotaCoefficientDetailDTOS) {
        this.quotaCoefficientDetailDTOS = quotaCoefficientDetailDTOS;
    }

    /*
     * 岗位类型
     */
    private Integer positionType;

    /*
     * 岗位类型
     */
    private String positionTypeName;

    /*
     * 积分各指标获得系数详情
     */
    private List<JyQuotaCoefficientDetailDTO> quotaCoefficientDetailDTOS;
}
