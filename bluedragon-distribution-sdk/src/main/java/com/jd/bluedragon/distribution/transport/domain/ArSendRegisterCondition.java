package com.jd.bluedragon.distribution.transport.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.math.BigDecimal;

/**
 * @author lixin39
 * @ClassName: ArSendRegisterCondition
 * @Description: 发货登记表-查询条件-这玩意真是难用
 * @date 2017年12月28日 09:46:12
 */
public class ArSendRegisterCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 航空单号/铁路单号
     */
    private String orderCode;

    /**
     * 运力名称
     */
    private String transportName;

    /**
     * 铁路站序
     */
    private String siteOrder;

    /**
     * 发货日期
     */
    private String sendDate;

    /**
     * 航空公司编号/铁路担当局编号
     */
    private String transCompanyCode;

    /**
     * 航空公司/铁路担当局名称
     */
    private String transCompany;

    /**
     * 起飞城市
     */
    private String startCityName;

    /**
     * 起飞城市编号
     */
    private Integer startCityId;

    /**
     * 落地城市
     */
    private String endCityName;

    /**
     * 落地城市编号
     */
    private Integer endCityId;

    /**
     * 起飞机场
     */
    private String startStationName;

    /**
     * 起飞机场编号
     */
    private String startStationId;

    /**
     * 落地机场
     */
    private String endStationName;

    /**
     * 落地机场编号
     */
    private String endStationId;

    /**
     * 预计起飞时间
     */
    private String planStartTime;

    /**
     * 预计落地时间
     */
    private String planEndTime;

    /**
     * 时效（跨越天数）
     */
    private Integer aging;

    /**
     * 发货件数
     */
    private Integer sendNum;

    /**
     * 计费重量
     */
    private BigDecimal chargedWeight;

    /**
     * 发货备注
     */
    private String remark;

    /**
     * 摆渡车型
     */
    private Integer shuttleBusType;

    /**
     * 摆渡车牌号
     */
    private String shuttleBusNum;

    /**
     * 批次号字符串
     */
    private String sendCode;

    /**
     * 操作部门
     */
    private String operationDept;

    /**
     * 操作开始时间
     */
    private String startOperTime;

    /**
     * 操作结束时间
     */
    private String endOperTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getSiteOrder() {
        return siteOrder;
    }

    public void setSiteOrder(String siteOrder) {
        this.siteOrder = siteOrder;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getTransCompanyCode() {
        return transCompanyCode;
    }

    public void setTransCompanyCode(String transCompanyCode) {
        this.transCompanyCode = transCompanyCode;
    }

    public String getTransCompany() {
        return transCompany;
    }

    public void setTransCompany(String transCompany) {
        this.transCompany = transCompany;
    }

    public String getStartCityName() {
        return startCityName;
    }

    public void setStartCityName(String startCityName) {
        this.startCityName = startCityName;
    }

    public Integer getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Integer startCityId) {
        this.startCityId = startCityId;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public Integer getAging() {
        return aging;
    }

    public void setAging(Integer aging) {
        this.aging = aging;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(String startStationId) {
        this.startStationId = startStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(String endStationId) {
        this.endStationId = endStationId;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public BigDecimal getChargedWeight() {
        return chargedWeight;
    }

    public void setChargedWeight(BigDecimal chargedWeight) {
        this.chargedWeight = chargedWeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getShuttleBusType() {
        return shuttleBusType;
    }

    public void setShuttleBusType(Integer shuttleBusType) {
        this.shuttleBusType = shuttleBusType;
    }

    public String getShuttleBusNum() {
        return shuttleBusNum;
    }

    public void setShuttleBusNum(String shuttleBusNum) {
        this.shuttleBusNum = shuttleBusNum;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getOperationDept() {
        return operationDept;
    }

    public void setOperationDept(String operationDept) {
        this.operationDept = operationDept;
    }

    public String getStartOperTime() {
        return startOperTime;
    }

    public void setStartOperTime(String startOperTime) {
        this.startOperTime = startOperTime;
    }

    public String getEndOperTime() {
        return endOperTime;
    }

    public void setEndOperTime(String endOperTime) {
        this.endOperTime = endOperTime;
    }

}
