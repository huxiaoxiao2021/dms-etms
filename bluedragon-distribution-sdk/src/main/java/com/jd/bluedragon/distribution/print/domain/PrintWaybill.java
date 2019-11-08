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
     * 运单类型
     */
    private Integer type ;

    /**
     * 拦截状态
     */
    private Integer statusCode ;

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

    private String newAddress;

    /**
     * 会员级别 V 企
     */
    private String userLevel;

    /**
    * 打包信息
    */
    private String packText;

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

    /**
     * 运单类型
     * @description type字段的转换（打印需要）
     * @see this.type
     */
    private Integer waybillType;

    /**
     * 包裹数量
     * quantity 的字段转换
     * @see this.quantity
     */
    private Integer packageCounter;

    /**
     * 商家ID
     * @see this.popSupId
     */
    private Integer companyId;

    /**
     * 商家名称
     * @see this.popSupName
     */
    private String companyName;

    /**
     * 是否已经打印发票
     * @see this.isPrintInvoice
     */
    private boolean hasPrintInvoice;

    /**
     * @see this.busiId
     */
    private Integer bCustomerId;

    /**
     * @see this.busiName
     */
    private String bCustomerName;

    /**
     * @see this.prepareSiteCode
     */
    private Integer printSiteCode;

    /**
     * 代收金额
     * @see this.packagePrice
     */
    private String receivable;

    /**
     * 是否是鸡毛信运单
     */
    private boolean featherLetterWaybill;

    /**
     * 是否需要打印
     * */
    private Boolean needPrintFlag = Boolean.TRUE;

    public PrintWaybill(){
        this.isAir=false;
        this.isSelfService=false;
        this.isPrintInvoice=false;
        this.featherLetterWaybill = false;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
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

    public String getPackText() {
        return packText;
    }

    public void setPackText(String packText) {
        this.packText = packText;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Integer getWaybillStatus() {
        return waybillStatus;
    }

    public void setWaybillStatus(Integer waybillStatus) {
        this.waybillStatus = waybillStatus;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }

    public Integer getPackageCounter() {
        return packageCounter;
    }

    public void setPackageCounter(Integer packageCounter) {
        this.packageCounter = packageCounter;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isHasPrintInvoice() {
        return hasPrintInvoice;
    }

    public void setHasPrintInvoice(boolean hasPrintInvoice) {
        this.hasPrintInvoice = hasPrintInvoice;
    }

    public Integer getbCustomerId() {
        return bCustomerId;
    }

    public void setbCustomerId(Integer bCustomerId) {
        this.bCustomerId = bCustomerId;
    }

    public String getbCustomerName() {
        return bCustomerName;
    }

    public void setbCustomerName(String bCustomerName) {
        this.bCustomerName = bCustomerName;
    }

    public Integer getPrintSiteCode() {
        return printSiteCode;
    }

    public void setPrintSiteCode(Integer printSiteCode) {
        this.printSiteCode = printSiteCode;
    }

    public String getReceivable() {
        return receivable;
    }

    public void setReceivable(String receivable) {
        this.receivable = receivable;
    }

    public boolean isFeatherLetterWaybill() {
        return featherLetterWaybill;
    }

    public void setFeatherLetterWaybill(boolean featherLetterWaybill) {
        this.featherLetterWaybill = featherLetterWaybill;
    }

    public Boolean getNeedPrintFlag() {
        return needPrintFlag;
    }

    public void setNeedPrintFlag(Boolean needPrintFlag) {
        this.needPrintFlag = needPrintFlag;
    }
}
