package com.jd.bluedragon.common.dto.integral.response;


import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/14
 */
public class JyBaseScoreRuleDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 指标编码
     */
    private String quotaNo;

    /**
     * 积分计算规则ID
     */
    private Long refCoefficientRule;

    /**
     * 显示顺序
     */
    private Byte displayOrder;

    /**
     * 组号
     */
    private String groupNo;

    /**
     * 大于
     */
    private BigDecimal gtValue;

    /**
     * 小于等于
     */
    private BigDecimal ltValue;

    /**
     * 是否为大于等于
     */
    private Integer gteSign;

    /**
     * 是否为小于等于
     */
    private Integer lteSign;

    /**
     * 1-加分；2-减分
     */
    private Byte symbol;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 创建人ERP
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新人ERP
     */
    private String updateUserErp;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：1-有效，0-删除
     */
    private Boolean yn;

    /**
     * 数据库时间
     */
    private Date ts;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuotaNo() {
        return quotaNo;
    }

    public void setQuotaNo(String quotaNo) {
        this.quotaNo = quotaNo;
    }

    public Long getRefCoefficientRule() {
        return refCoefficientRule;
    }

    public void setRefCoefficientRule(Long refCoefficientRule) {
        this.refCoefficientRule = refCoefficientRule;
    }

    public Byte getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Byte displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public BigDecimal getGtValue() {
        return gtValue;
    }

    public void setGtValue(BigDecimal gtValue) {
        this.gtValue = gtValue;
    }

    public BigDecimal getLtValue() {
        return ltValue;
    }

    public void setLtValue(BigDecimal ltValue) {
        this.ltValue = ltValue;
    }

    public Integer getGteSign() {
        return gteSign;
    }

    public void setGteSign(Integer gteSign) {
        this.gteSign = gteSign;
    }

    public Integer getLteSign() {
        return lteSign;
    }

    public void setLteSign(Integer lteSign) {
        this.lteSign = lteSign;
    }

    public Byte getSymbol() {
        return symbol;
    }

    public void setSymbol(Byte symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
