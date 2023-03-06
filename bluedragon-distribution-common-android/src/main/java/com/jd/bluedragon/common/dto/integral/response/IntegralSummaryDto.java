package com.jd.bluedragon.common.dto.integral.response;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName IntegralSummaryDto
 * @Description
 * @Author wyh
 * @Date 2023/3/6 15:51
 **/
public class IntegralSummaryDto implements Serializable {

    private static final long serialVersionUID = -5102975835843217088L;

    /**
     * 日期
     */
    private String date;

    /**
     * 月份。eg 01月 10月
     */
    private String month;

    /**
     * 年份。 eg 2023
     */
    private String year;

    /**
     * 总积分。日或月总积分
     */
    private BigDecimal totalIntegral;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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

    public BigDecimal getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(BigDecimal totalIntegral) {
        this.totalIntegral = totalIntegral;
    }
}
