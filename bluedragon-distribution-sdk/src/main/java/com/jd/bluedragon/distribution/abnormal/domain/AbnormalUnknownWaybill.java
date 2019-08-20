package com.jd.bluedragon.distribution.abnormal.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybill
 * @Description: 三无订单申请-实体类
 * @date 2018年05月08日 15:16:15
 */
public class AbnormalUnknownWaybill extends DbEntity {

    private static final long serialVersionUID = 1L;

    public static final String RECEIPT_FROM_WAYBILL = "W";//运单
    public static final String RECEIPT_FROM_ECLP = "E";//eclp
    public static final String RECEIPT_FROM_B = "B";//商家回复
    public static final Integer ISRECEIPT_YES = 1;//已回复
    public static final Integer ISRECEIPT_NO = 0;//未回复
    public static final Integer ORDERNUMBER_0 = 0;//第0次
    public static final Integer ORDERNUMBER_1 = 1;//第一次
    public static final Integer ORDERNUMBER_DEFAULT_MAX = 1;//默认最大次数
    public static final Integer WAYBILL_SIZE_DEFAULT_MAX = 1000;//默认最大次数
    public static final Integer REPORT_YES = 1;//查询并上报
    public static final String SEPARATOR_APPEND = ",";//分隔符
    public static final String SEPARATOR_SPLIT = "\\n";//批量运单号分割
    public static final Integer DELIVERED_WAYBILL_NOTICE_MAX_COUNT = 10;//妥投提示运单的最大数量
    /**
     * 运单号
     */
    private String waybillCode;

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
     * 回复系统 W运单, E ECLE,  B  B商家端
     */
    private String receiptFrom;

    /**
     * 是否上报（请求时 发过来）
     */
    private Integer isReport;

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

    public String getReceiptFrom() {
        return receiptFrom;
    }

    public void setReceiptFrom(String receiptFrom) {
        this.receiptFrom = receiptFrom;
    }

    public Integer getIsReport() {
        return isReport;
    }

    public void setIsReport(Integer isReport) {
        this.isReport = isReport;
    }
}
