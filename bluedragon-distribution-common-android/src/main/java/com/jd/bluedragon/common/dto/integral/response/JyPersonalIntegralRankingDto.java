package com.jd.bluedragon.common.dto.integral.response;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName JyPersonalIntegralRankingDto
 * @Description
 * @Author wyh
 * @Date 2023/3/6 16:44
 **/
public class JyPersonalIntegralRankingDto implements Serializable {

    private static final long serialVersionUID = 1145990919104881272L;

    /**
     * 当前排名
     */
    private Integer ranking;

    /**
     * 和上一名差距
     */
    private BigDecimal gapBetweenPre;

    /**
     * 总积分
     */
    private BigDecimal totalIntegral;

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public BigDecimal getGapBetweenPre() {
        return gapBetweenPre;
    }

    public void setGapBetweenPre(BigDecimal gapBetweenPre) {
        this.gapBetweenPre = gapBetweenPre;
    }

    public BigDecimal getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(BigDecimal totalIntegral) {
        this.totalIntegral = totalIntegral;
    }
}
