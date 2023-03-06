package com.jd.bluedragon.common.dto.integral.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName JyIntegralRankingDTO
 * @Description 个人积分排行榜实体
 * @Author wyh
 * @Date 2023/3/6 15:13
 **/
public class JyIntegralRankingDTO implements Serializable {

    private static final long serialVersionUID = -9178051524395177499L;

    /**
     * 用户ERP或身份证号
     */
    private String userCode;

    private String userName;

    /**
     * 用户归属场地
     */
    private Integer siteCode;

    private String siteName;

    /**
     * 排名
     */
    private Integer ranking;

    /**
     * 总积分
     */
    private BigDecimal integral;

    /**
     * 积分排行榜更新时间
     */
    private Date integralUpdateTime;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public BigDecimal getIntegral() {
        return integral;
    }

    public void setIntegral(BigDecimal integral) {
        this.integral = integral;
    }

    public Date getIntegralUpdateTime() {
        return integralUpdateTime;
    }

    public void setIntegralUpdateTime(Date integralUpdateTime) {
        this.integralUpdateTime = integralUpdateTime;
    }
}
