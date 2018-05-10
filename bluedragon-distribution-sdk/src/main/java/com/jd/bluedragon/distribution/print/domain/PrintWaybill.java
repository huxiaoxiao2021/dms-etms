package com.jd.bluedragon.distribution.print.domain;

import java.util.List;

/**
 * 运单打印基本信息
 * Created by wangtingwei on 2015/12/23.
 */
public class PrintWaybill extends BasePrintWaybill {

    private static final long serialVersionUID = 1L;

    private transient Boolean isAir;

    private transient Boolean isSelfService;

    private Integer orgId;

    private Integer cky2;



    /**
     * 库房号
     */
    private String storeName;

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
    private Integer quantity ;

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

    private Integer purposefulDmsCode;

    private String newAddress;

    /**
     * 会员级别 V 企
     */
    private String userLevel;
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

    private Integer prepareSiteCode ;
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
    * 客户姓名
    */
    private String customerName ;

    /**
    * 客户电话
    */
    private String customerContacts ;

    /**
     * 客户联系方式 tmsWaybill.getReceiverMobile(),tmsWaybill.getReceiverTel()
     */
    private String mobileFirst;
    private String mobileLast;

    private String telFirst;
    private String telLast;

    private Integer distributeType;

    private String sendPay = "";

    private String waybillSign = "";

    public Integer getDistributeType() {
        return distributeType;
    }

    public void setDistributeType(Integer distributeType) {
        this.distributeType = distributeType;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

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
    * 服务号
    */
    private String serviceCode;

    /**
    * 包裹列表
    */
    private List<PrintPackage> packList ;

    /**
     * 运单状态
     */
    private Integer waybillStatus ;

    public PrintWaybill(){
        this.isAir=false;
        this.isSelfService=false;
        this.isPrintInvoice=false;

    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public Integer getPurposefulDmsCode() {
        return purposefulDmsCode;
    }

    public void setPurposefulDmsCode(Integer purposefulDmsCode) {
        this.purposefulDmsCode = purposefulDmsCode;
    }

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
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

    public Integer getPrepareSiteCode() {
        return prepareSiteCode;
    }

    public void setPrepareSiteCode(Integer prepareSiteCode) {
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getCky2() {
        return cky2;
    }

    public void setCky2(Integer cky2) {
        this.cky2 = cky2;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Boolean getIsAir() {
        return isAir;
    }

    public void setIsAir(Boolean isAir) {
        this.isAir = isAir;
    }

    public Boolean getIsSelfService() {
        return isSelfService;
    }

    public void setIsSelfService(Boolean isSelfService) {
        this.isSelfService = isSelfService;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getTelFirst() {
        return telFirst;
    }

    public void setTelFirst(String telFirst) {
        this.telFirst = telFirst;
    }

    public String getTelLast() {
        return telLast;
    }

    public void setTelLast(String telLast) {
        this.telLast = telLast;
    }

    public String getMobileLast() {
        return mobileLast;
    }

    public void setMobileLast(String mobileLast) {
        this.mobileLast = mobileLast;
    }

    public String getMobileFirst() {
        return mobileFirst;
    }

    public void setMobileFirst(String mobileFirst) {
        this.mobileFirst = mobileFirst;
    }

    public Integer getWaybillStatus() {
        return waybillStatus;
    }

    public void setWaybillStatus(Integer waybillStatus) {
        this.waybillStatus = waybillStatus;
    }
}
