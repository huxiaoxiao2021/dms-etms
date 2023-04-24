package com.jd.bluedragon.common.dto.integral.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName JyIntegralMonthlySummaryDto
 * @Description
 * @Author wyh
 * @Date 2023/3/6 15:51
 **/
public class JyIntegralMonthlySummaryDto implements Serializable {

    private static final long serialVersionUID = -5102975835843217088L;

    /**
     * 年份。 eg 2023
     */
    private String year;

    /**
     * 每月积分情况
     */
    private List<JyIntegralMonthlySummaryDto.MonthlyIntegral> monthlyIntegralList;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<MonthlyIntegral> getMonthlyIntegralList() {
        return monthlyIntegralList;
    }

    public void setMonthlyIntegralList(List<MonthlyIntegral> monthlyIntegralList) {
        this.monthlyIntegralList = monthlyIntegralList;
    }

    public static class MonthlyIntegral implements Serializable {

        private static final long serialVersionUID = 4768252535185156102L;

        private String month;

        /**
         * 总积分
         */
        private BigDecimal totalIntegral;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public BigDecimal getTotalIntegral() {
            return totalIntegral;
        }

        public void setTotalIntegral(BigDecimal totalIntegral) {
            this.totalIntegral = totalIntegral;
        }
    }
}
