package com.jd.bluedragon.distribution.abnormal.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;
import java.util.List;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillCondition
 * @Description: 三无订单申请-查询条件
 * @date 2018年05月08日 15:16:15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbnormalUnknownWaybillCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 一次查多个
     */
    private List<String> waybillCodes;

    /**
     * 序号-标识该运单第几次提出申请
     */
    private Integer orderNumber;

    /**
     * 商家ID
     */
    private Integer traderId;

    /**
     * 商家名称
     */
    private String traderName;

    /**
     * 上报机构id
     */
    private Integer dmsSiteCode;

    /**
     * 上报机构名称
     */
    private String dmsSiteName;

    /**
     * 上报区域ID
     */
    private Integer areaId;

    /**
     * 上报区域名称
     */
    private String areaName;

    /**
     * 回复时间
     */
    private Date receiptTime;

    /**
     * 是否回复
     */
    private Integer isReceipt;

    /**  */
    private String receiptContent;

    /**
     * 创建人code
     */
    private Integer createUserCode;

    /**
     * 创建人ERP
     */
    private String createUser;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 发起起始时间
     */
    private Date startTime;

    /**
     * 发起截止时间
     */
    private Date endTime;

    /**
     * 省区编码
     */
    private String provinceAgencyCode;
    /**
     * 枢纽编码
     */
    private String areaHubCode;

    /**
     * The set method for waybillCode.
     *
     * @param waybillCode
     */
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    /**
     * The get method for waybillCode.
     *
     * @return this.waybillCode
     */
    public String getWaybillCode() {
        return this.waybillCode;
    }

    /**
     * The set method for orderNumber.
     *
     * @param orderNumber
     */
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * The get method for orderNumber.
     *
     * @return this.orderNumber
     */
    public Integer getOrderNumber() {
        return this.orderNumber;
    }

    /**
     * The set method for traderId.
     *
     * @param traderId
     */
    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    /**
     * The get method for traderId.
     *
     * @return this.traderId
     */
    public Integer getTraderId() {
        return this.traderId;
    }

    /**
     * The set method for traderName.
     *
     * @param traderName
     */
    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    /**
     * The get method for traderName.
     *
     * @return this.traderName
     */
    public String getTraderName() {
        return this.traderName;
    }

    /**
     * The set method for dmsSiteCode.
     *
     * @param dmsSiteCode
     */
    public void setDmsSiteCode(Integer dmsSiteCode) {
        this.dmsSiteCode = dmsSiteCode;
    }

    /**
     * The get method for dmsSiteCode.
     *
     * @return this.dmsSiteCode
     */
    public Integer getDmsSiteCode() {
        return this.dmsSiteCode;
    }

    /**
     * The set method for dmsSiteName.
     *
     * @param dmsSiteName
     */
    public void setDmsSiteName(String dmsSiteName) {
        this.dmsSiteName = dmsSiteName;
    }

    /**
     * The get method for dmsSiteName.
     *
     * @return this.dmsSiteName
     */
    public String getDmsSiteName() {
        return this.dmsSiteName;
    }

    /**
     * The set method for areaId.
     *
     * @param areaId
     */
    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    /**
     * The get method for areaId.
     *
     * @return this.areaId
     */
    public Integer getAreaId() {
        return this.areaId;
    }

    /**
     * The set method for areaName.
     *
     * @param areaName
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * The get method for areaName.
     *
     * @return this.areaName
     */
    public String getAreaName() {
        return this.areaName;
    }

    /**
     * The set method for receiptTime.
     *
     * @param receiptTime
     */
    public void setReceiptTime(Date receiptTime) {
        this.receiptTime = receiptTime;
    }

    /**
     * The get method for receiptTime.
     *
     * @return this.receiptTime
     */
    public Date getReceiptTime() {
        return this.receiptTime;
    }

    /**
     * The set method for isReceipt.
     *
     * @param isReceipt
     */
    public void setIsReceipt(Integer isReceipt) {
        this.isReceipt = isReceipt;
    }

    /**
     * The get method for isReceipt.
     *
     * @return this.isReceipt
     */
    public Integer getIsReceipt() {
        return this.isReceipt;
    }

    /**
     * The set method for receiptContent.
     *
     * @param receiptContent
     */
    public void setReceiptContent(String receiptContent) {
        this.receiptContent = receiptContent;
    }

    /**
     * The get method for receiptContent.
     *
     * @return this.receiptContent
     */
    public String getReceiptContent() {
        return this.receiptContent;
    }

    /**
     * The set method for createUserCode.
     *
     * @param createUserCode
     */
    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    /**
     * The get method for createUserCode.
     *
     * @return this.createUserCode
     */
    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    /**
     * The set method for createUser.
     *
     * @param createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * The get method for createUser.
     *
     * @return this.createUser
     */
    public String getCreateUser() {
        return this.createUser;
    }

    /**
     * The set method for createUserName.
     *
     * @param createUserName
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    /**
     * The get method for createUserName.
     *
     * @return this.createUserName
     */
    public String getCreateUserName() {
        return this.createUserName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getWaybillCodes() {
        return waybillCodes;
    }

    public void setWaybillCodes(List<String> waybillCodes) {
        this.waybillCodes = waybillCodes;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }
}
