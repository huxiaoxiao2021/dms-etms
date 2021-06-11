package com.jd.bluedragon.external.crossbow.itms.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ItmsSendDetailDto
 * @Description 发货数据
 * @Author wyh
 * @Date 2021/6/7 20:18
 **/
public class ItmsSendDetailDto implements Serializable {

    private static final long serialVersionUID = -3521937338722743425L;

    /**
     * 租户编码
     */
    private String partnerNo;

    /**
     * 交接单号
     */
    private String receiptCode;

    /**
     * 始发网点
     */
    private String fromLocationId;

    /**
     * 始发网点名称
     */
    private String fromLocationIdName;

    /**
     * 目的网点
     */
    private String toLocationId;

    /**
     * 目的网点名称
     */
    private String toLocationIdName;

    /**
     * 操作时间
     */
    private String opeTime;

    /**
     * 来源：1-分拣，2-终端
     */
    private Integer source;

    /**
     * 包裹明细
     */
    private List<ItmsPackageDetail> packageDetailList;

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public String getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(String fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public String getFromLocationIdName() {
        return fromLocationIdName;
    }

    public void setFromLocationIdName(String fromLocationIdName) {
        this.fromLocationIdName = fromLocationIdName;
    }

    public String getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(String toLocationId) {
        this.toLocationId = toLocationId;
    }

    public String getToLocationIdName() {
        return toLocationIdName;
    }

    public void setToLocationIdName(String toLocationIdName) {
        this.toLocationIdName = toLocationIdName;
    }

    public String getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(String opeTime) {
        this.opeTime = opeTime;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public List<ItmsPackageDetail> getPackageDetailList() {
        return packageDetailList;
    }

    public void setPackageDetailList(List<ItmsPackageDetail> packageDetailList) {
        this.packageDetailList = packageDetailList;
    }
}
