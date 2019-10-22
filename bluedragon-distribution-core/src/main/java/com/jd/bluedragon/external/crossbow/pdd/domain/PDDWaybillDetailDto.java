package com.jd.bluedragon.external.crossbow.pdd.domain;

import java.util.List;

/**
 * <p>
 *     拼多多运单对象实体
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
public class PDDWaybillDetailDto {

    /**
     * 商家ID
     */
    private Long mallId;

    /**
     * 快递公司编码
     */
    private String wpCode;

    /**
     * 面单版本号
     */
    private Integer version;

    /**
     * 电子面单号
     */
    private String waybillCode;

    /**
     * 网点编码
     */
    private String branchCode;

    /**
     * 寄件人姓名
     */
    private String senderName;

    /**
     * 寄件人电话: 021-31233121
     */
    private String senderPhone;

    /**
     * 寄件人手机: 13812345678
     */
    private String senderMobile;

    /**
     * 寄件人地址
     */
    private PDDAddressDto senderAddress;

    /**
     * 收件人姓名
     */
    private String consigneeName;

    /**
     * 收件人电话
     */
    private String consigneePhone;

    /**
     * 收件人手机
     */
    private String consigneeMobile;

    /**
     * 收件人地址
     */
    private PDDAddressDto consigneeAddress;

    /**
     * 大头笔 示例值：杭州
     */
    private String datoubi;

    /**
     * 商品信息列表
     */
    private List<PDDGoodsItem> itemList;

    /**
     * 集包地
     */
    private String consolidationName;

    /**
     * 集包地编码
     */
    private String consolidationCode;

    /**
     * 体积：单位 ml
     */
    private Integer volume;

    /**
     * 重量：单位 g
     */
    private Integer weight;

    /**
     * 订单渠道类型
     */
    private String orderChannelsType;

    /**
     * 订单列表
     */
    private List<String> bizIdList;

    /**
     * 1-取号成功 3-已取消
     */
    private Integer status;

    /**
     * 集包地编码
     */
    private String wrapCode;

    /**
     * 集包地名称
     */
    private String wrapName;

    /**
     * 创建时间： yyyy-MM-dd hh:mm:ss.SSS
     */
    private String createTime;

    /**
     * 面单编码规则：NORMAL
     */
    private String segmentCode;

    public Long getMallId() {
        return mallId;
    }

    public void setMallId(Long mallId) {
        this.mallId = mallId;
    }

    public String getWpCode() {
        return wpCode;
    }

    public void setWpCode(String wpCode) {
        this.wpCode = wpCode;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public PDDAddressDto getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(PDDAddressDto senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeMobile() {
        return consigneeMobile;
    }

    public void setConsigneeMobile(String consigneeMobile) {
        this.consigneeMobile = consigneeMobile;
    }

    public PDDAddressDto getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(PDDAddressDto consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public String getDatoubi() {
        return datoubi;
    }

    public void setDatoubi(String datoubi) {
        this.datoubi = datoubi;
    }

    public List<PDDGoodsItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<PDDGoodsItem> itemList) {
        this.itemList = itemList;
    }

    public String getConsolidationName() {
        return consolidationName;
    }

    public void setConsolidationName(String consolidationName) {
        this.consolidationName = consolidationName;
    }

    public String getConsolidationCode() {
        return consolidationCode;
    }

    public void setConsolidationCode(String consolidationCode) {
        this.consolidationCode = consolidationCode;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getOrderChannelsType() {
        return orderChannelsType;
    }

    public void setOrderChannelsType(String orderChannelsType) {
        this.orderChannelsType = orderChannelsType;
    }

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getWrapCode() {
        return wrapCode;
    }

    public void setWrapCode(String wrapCode) {
        this.wrapCode = wrapCode;
    }

    public String getWrapName() {
        return wrapName;
    }

    public void setWrapName(String wrapName) {
        this.wrapName = wrapName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSegmentCode() {
        return segmentCode;
    }

    public void setSegmentCode(String segmentCode) {
        this.segmentCode = segmentCode;
    }
}
