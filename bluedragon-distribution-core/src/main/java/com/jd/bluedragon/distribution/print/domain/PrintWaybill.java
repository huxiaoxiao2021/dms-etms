package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 运单打印基本信息
 * Created by wangtingwei on 2015/12/23.
 */
public class PrintWaybill implements Serializable {

    /**
     * 运单号
     */
    private String waybillCode ;

    /**
     * 运单类型
     */
    private int type ;

    /**
     * 拦截状态
     */
    private int statusCode ;

    /**
     * 拦截状态提示信息
     */
    private String statusMessage ;

    /**
    * 打包数量
    */
    private int quantity ;

    /**
    * 10987POP商家ID号
    */
    private Integer popSupId ;

    /**
    * POP商家名称
    */
    private String popSupName ;

    /**
    * 是否已经打印发票
    */
    private boolean isPrintInvoice ;

    /**
     * B商家ID
     */
    private Integer busiId ;

    /**
     * B商家名称
     */
    private String busiName ;

    /**
    * 库房号
    */
    private Integer StoreId ;

    /**
    * 奢  标识文本
    */
    private String luxuryText ;

    /**
    * 普 标识文本
    */
    private String normalText ;

    /**
    * 始发分拣中心
    */
    
    

    private String originalDmsName;
    /**
    * 目的分拣中心
    */
    
    private String purposefulDmsName;
    /**
    * 原始宠车号
    */
    private String originalTabletrolley ;

    /**
    * 目的宠车号
    */
    private String purposefulTableTrolley ;

    /**
    * 始发道口号
    */
    private String originalCrossCode ;

    /**
    * 目的道口号
    */
    private String purposefulCrossCode ;

    /**
    * 站点名称
    */
    private String prepareSiteName ;

    private String prepareSiteCode ;
    /**
    * 收货地址
    */
    private String printAddress ;



    /**
    * 承诺配送信息
    */
    private String promiseText ;

    /**
    * 打包信息
    */
    private String packText;
    /**
    * 时效
    */
    private String timeCategory ;

    /**
    * 库房号
    */
    private String warehouseText ;

    /**
    * 支付方式信息
    */
    private String paymentText ;

    /**
    * 代收金额
    */
    private String packagePrice;

    /**
    * 包裹特殊标识
    */
    private String specialMark ;

    /**
    * 客户姓名
    */
    private String customerName ;

    /**
    * 客户电话
    */
    private String customerContacts ;

    /**
    * 
    */
    private String remark ;

    private String comment ;

    /**
    * 订单号
    */
    private String orderCode;
    /**
    * 新用记标识
    */
    private String newCustomerText ;

    /**
    * 路区
    */
    private String road;

    /**
    * 服务号
    */
    private String serviceCode;

    /**
    * 包裹列表
    */
    private List<PrintPackage> packList ;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getPopSupId() {
        return popSupId;
    }

    public void setPopSupId(Integer popSupId) {
        this.popSupId = popSupId;
    }

    public String getPopSupName() {
        return popSupName;
    }

    public void setPopSupName(String popSupName) {
        this.popSupName = popSupName;
    }

    public boolean isPrintInvoice() {
        return isPrintInvoice;
    }

    public void setPrintInvoice(boolean isPrintInvoice) {
        this.isPrintInvoice = isPrintInvoice;
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

    public Integer getStoreId() {
        return StoreId;
    }

    public void setStoreId(Integer storeId) {
        StoreId = storeId;
    }

    public String getLuxuryText() {
        return luxuryText;
    }

    public void setLuxuryText(String luxuryText) {
        this.luxuryText = luxuryText;
    }

    public String getNormalText() {
        return normalText;
    }

    public void setNormalText(String normalText) {
        this.normalText = normalText;
    }

    public String getOriginalDmsName() {
        return originalDmsName;
    }

    public void setOriginalDmsName(String originalDmsName) {
        this.originalDmsName = originalDmsName;
    }

    public String getPurposefulDmsName() {
        return purposefulDmsName;
    }

    public void setPurposefulDmsName(String purposefulDmsName) {
        this.purposefulDmsName = purposefulDmsName;
    }

    public String getOriginalTabletrolley() {
        return originalTabletrolley;
    }

    public void setOriginalTabletrolley(String originalTabletrolley) {
        this.originalTabletrolley = originalTabletrolley;
    }

    public String getPurposefulTableTrolley() {
        return purposefulTableTrolley;
    }

    public void setPurposefulTableTrolley(String purposefulTableTrolley) {
        this.purposefulTableTrolley = purposefulTableTrolley;
    }

    public String getOriginalCrossCode() {
        return originalCrossCode;
    }

    public void setOriginalCrossCode(String originalCrossCode) {
        this.originalCrossCode = originalCrossCode;
    }

    public String getPurposefulCrossCode() {
        return purposefulCrossCode;
    }

    public void setPurposefulCrossCode(String purposefulCrossCode) {
        this.purposefulCrossCode = purposefulCrossCode;
    }

    public String getPrepareSiteName() {
        return prepareSiteName;
    }

    public void setPrepareSiteName(String prepareSiteName) {
        this.prepareSiteName = prepareSiteName;
    }

    public String getPrepareSiteCode() {
        return prepareSiteCode;
    }

    public void setPrepareSiteCode(String prepareSiteCode) {
        this.prepareSiteCode = prepareSiteCode;
    }

    public String getPrintAddress() {
        return printAddress;
    }

    public void setPrintAddress(String printAddress) {
        this.printAddress = printAddress;
    }

    public String getPromiseText() {
        return promiseText;
    }

    public void setPromiseText(String promiseText) {
        this.promiseText = promiseText;
    }

    public String getPackText() {
        return packText;
    }

    public void setPackText(String packText) {
        this.packText = packText;
    }

    public String getTimeCategory() {
        return timeCategory;
    }

    public void setTimeCategory(String timeCategory) {
        this.timeCategory = timeCategory;
    }

    public String getWarehouseText() {
        return warehouseText;
    }

    public void setWarehouseText(String warehouseText) {
        this.warehouseText = warehouseText;
    }

    public String getPaymentText() {
        return paymentText;
    }

    public void setPaymentText(String paymentText) {
        this.paymentText = paymentText;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getSpecialMark() {
        return specialMark;
    }

    public void setSpecialMark(String specialMark) {
        this.specialMark = specialMark;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContacts() {
        return customerContacts;
    }

    public void setCustomerContacts(String customerContacts) {
        this.customerContacts = customerContacts;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getNewCustomerText() {
        return newCustomerText;
    }

    public void setNewCustomerText(String newCustomerText) {
        this.newCustomerText = newCustomerText;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public List<PrintPackage> getPackList() {
        return packList;
    }

    public void setPackList(List<PrintPackage> packList) {
        this.packList = packList;
    }
}
