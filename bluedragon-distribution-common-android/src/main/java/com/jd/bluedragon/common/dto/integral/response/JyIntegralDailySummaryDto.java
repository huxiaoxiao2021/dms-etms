package com.jd.bluedragon.common.dto.integral.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName JyIntegralDailySummaryDto
 * @Description
 * @Author wyh
 * @Date 2023/3/6 15:51
 **/
public class JyIntegralDailySummaryDto implements Serializable {

    private static final long serialVersionUID = -5102975835843217088L;

    /**
     * 月份。eg 01月 10月
     */
    private String month;

    /**
     * 年份。 eg 2023
     */
    private String year;

    /**
     * 每日积分情况
     */
    private List<DailyIntegral> dailyIntegralList;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<DailyIntegral> getDailyIntegralList() {
        return dailyIntegralList;
    }

    public void setDailyIntegralList(List<DailyIntegral> dailyIntegralList) {
        this.dailyIntegralList = dailyIntegralList;
    }

    public static class DailyIntegral implements Serializable {

        private static final long serialVersionUID = 4768252535185156102L;

        private String date;

        /**
         * 总积分
         */
        private BigDecimal totalIntegral;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public BigDecimal getTotalIntegral() {
            return totalIntegral;
        }

        public void setTotalIntegral(BigDecimal totalIntegral) {
            this.totalIntegral = totalIntegral;
        }
    }
}
