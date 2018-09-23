package com.jd.bluedragon.distribution.rma.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *  RMA交接清单打印主表-运单维度
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public class RmaHandoverWaybill implements Serializable {

    private static final long serialVersionUID = -7800839106682310757L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹数量
     */
    private Integer packageCount;

    /**
     * 发货操作人名称
     */
    private String sendUserName;

    /**
     * 发货操作人编号
     */
    private Integer sendUserCode;

    /**
     * 发货操作人电话
     */
    private String sendUserMobile;

    /**
     * 发货日期
     */
    private Date sendDate;

    /**
     * 操作站点编号
     */
    private Integer createSiteCode;

    /**
     * 操作站点名称
     */
    private String createSiteName;

    /**
     * 发货省编号
     */
    private Integer sendProvinceId;

    /**
     * 发货省名称
     */
    private String sendProvinceName;

    /**
     * 发货城市编号
     */
    private Integer sendCityId;

    /**
     * 发货城市名称
     */
    private String sendCityName;

    /**
     * 目的省编号
     */
    private Integer targetProvinceId;

    /**
     * 目的省名称
     */
    private String targetProvinceName;

    /**
     * 目的城市编号
     */
    private Integer targetCityId;

    /**
     * 目的城市名称
     */
    private String targetCityName;

    /**
     * B商家ID
     */
    private Integer busiId;

    /**
     * 商家名称
     */
    private String busiName;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人电话
     */
    private String receiverMobile;

    /**
     * 收货人地址
     */
    private String receiverAddress;

    /**
     * 是否已打印 0-未打印，1-已打印
     */
    private Integer printStatus;

    /**
     * 打印时间
     */
    private Date printTime;

    /**
     * 打印操作人名称
     */
    private String printUserName;

    /**
     * 打印操作人编号
     */
    private Integer printUserCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0-已删除，1-未删除
     */
    private Integer yn;

    /**
     * RMA交接明细记录
     */
    private List<RmaHandoverDetail> rmaHandoverDetail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public Integer getSendUserCode() {
        return sendUserCode;
    }

    public void setSendUserCode(Integer sendUserCode) {
        this.sendUserCode = sendUserCode;
    }

    public String getSendUserMobile() {
        return sendUserMobile;
    }

    public void setSendUserMobile(String sendUserMobile) {
        this.sendUserMobile = sendUserMobile;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getSendProvinceId() {
        return sendProvinceId;
    }

    public void setSendProvinceId(Integer sendProvinceId) {
        this.sendProvinceId = sendProvinceId;
    }

    public String getSendProvinceName() {
        return sendProvinceName;
    }

    public void setSendProvinceName(String sendProvinceName) {
        this.sendProvinceName = sendProvinceName;
    }

    public Integer getSendCityId() {
        return sendCityId;
    }

    public void setSendCityId(Integer sendCityId) {
        this.sendCityId = sendCityId;
    }

    public String getSendCityName() {
        return sendCityName;
    }

    public void setSendCityName(String sendCityName) {
        this.sendCityName = sendCityName;
    }

    public Integer getTargetProvinceId() {
        return targetProvinceId;
    }

    public void setTargetProvinceId(Integer targetProvinceId) {
        this.targetProvinceId = targetProvinceId;
    }

    public String getTargetProvinceName() {
        return targetProvinceName;
    }

    public void setTargetProvinceName(String targetProvinceName) {
        this.targetProvinceName = targetProvinceName;
    }

    public Integer getTargetCityId() {
        return targetCityId;
    }

    public void setTargetCityId(Integer targetCityId) {
        this.targetCityId = targetCityId;
    }

    public String getTargetCityName() {
        return targetCityName;
    }

    public void setTargetCityName(String targetCityName) {
        this.targetCityName = targetCityName;
    }

    public Integer getBusiId() {
        return busiId;
    }

    public void setBusiId(Integer busiId) {
        this.busiId = busiId;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Integer getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(Integer printStatus) {
        this.printStatus = printStatus;
    }

    public Date getPrintTime() {
        return printTime;
    }

    public void setPrintTime(Date printTime) {
        this.printTime = printTime;
    }

    public String getPrintUserName() {
        return printUserName;
    }

    public void setPrintUserName(String printUserName) {
        this.printUserName = printUserName;
    }

    public Integer getPrintUserCode() {
        return printUserCode;
    }

    public void setPrintUserCode(Integer printUserCode) {
        this.printUserCode = printUserCode;
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

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public List<RmaHandoverDetail> getRmaHandoverDetail() {
        return rmaHandoverDetail;
    }

    public void setRmaHandoverDetail(List<RmaHandoverDetail> rmaHandoverDetail) {
        this.rmaHandoverDetail = rmaHandoverDetail;
    }
}
