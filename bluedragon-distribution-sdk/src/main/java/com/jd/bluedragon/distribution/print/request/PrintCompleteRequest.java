package com.jd.bluedragon.distribution.print.request;

import java.io.Serializable;

/**
 * @author wyh
 * @className PrintCompleteRequest
 * @description
 * @date 2021/11/24 14:58
 **/
public class PrintCompleteRequest implements Serializable {

    private static final long serialVersionUID = -4428873050145686682L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageBarcode;

    /**
     * 操作人编号
     */
    private Integer operatorCode;

    /**
     * 操作人ERP
     */
    private String operatorErp;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作人站点编号
     */
    private Integer operateSiteCode;

    /**
     * 操作人站点名称
     */
    private String operateSiteName;

    /**
     * 操作类型 1：打印包裹；2：打印发票；
     */
    private Integer operateType;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * POP商家ID
     */
    private Integer popSupId;

    /**
     * POP商家名称
     */
    private String popSupName;

    /**
     * 包裹数量
     */
    private Integer quantity;

    /**
     * 滑道号
     */
    private String crossCode;

    /** 运单类型(JYN) */
    private Integer waybillType;

    /**
     * POP收货类型：
     * 	商家直送：1
     * 	托寄送货：2
     * 	司机接货：3
     *  配送员接货：4
     *  驻场打印：5
     *  外单逆向换单：6
     */
    private Integer popReceiveType;

    /**
     * 三方运单号
     */
    private String thirdWaybillCode;

    /**
     * 排队号
     */
    private String queueNo;

    private String boxCode;

    private String driverCode;

    private String driverName;

    /**
     * B商家ID
     */
    private Integer busiId;

    /**
     * B商家名称
     */
    private String busiName;

    /**
     * 业务类型-区分1-平台打印和2-站点平台打印
     */
    private Integer businessType;

    /**
     *打印入口类型
     */
    private Integer interfaceType;

    /**
     * 包裹托寄物名称
     */
    private String categoryName;


    /**
     * 运单waybillSign
     */
    private String waybillSign;

    /**
     * 打印结果-模板分组(区分B网和C网),TemplateGroupEnum对应的code
     */
    private String templateGroupCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板版本-默认为0，最后一个版本号
     */
    private Integer templateVersion = 0;

    /**
     * 是否是分拣中心首次打印 1：是 0：否
     */
    private Integer sortingFirstPrint;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }

    public Integer getPopReceiveType() {
        return popReceiveType;
    }

    public void setPopReceiveType(Integer popReceiveType) {
        this.popReceiveType = popReceiveType;
    }

    public String getThirdWaybillCode() {
        return thirdWaybillCode;
    }

    public void setThirdWaybillCode(String thirdWaybillCode) {
        this.thirdWaybillCode = thirdWaybillCode;
    }

    public String getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(String queueNo) {
        this.queueNo = queueNo;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Integer interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public String getTemplateGroupCode() {
        return templateGroupCode;
    }

    public void setTemplateGroupCode(String templateGroupCode) {
        this.templateGroupCode = templateGroupCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(Integer templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Integer getSortingFirstPrint() {
        return sortingFirstPrint;
    }

    public void setSortingFirstPrint(Integer sortingFirstPrint) {
        this.sortingFirstPrint = sortingFirstPrint;
    }
}
