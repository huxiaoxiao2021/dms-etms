package com.jd.bluedragon.external.crossbow.itms.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ItmsCancelSendDto
 * @Description 取消发货数据
 * @Author wyh
 * @Date 2021/6/7 20:28
 **/
public class ItmsCancelSendDto implements Serializable {

    private static final long serialVersionUID = 2738998965451163309L;

    /**
     * 租户编码
     */
    private String partnerNo;

    /**
     * 交接单号
     */
    private String receiptCode;

    /**
     * 操作网点
     */
    private String opeSiteId;

    /**
     * 操作网点名称
     */
    private String opeSiteName;

    /**
     * 操作时间
     */
    private String opeTime;

    /**
     * 包裹明细
     */
    private List<ItmsPackageDetail> packageDetailList;

    /**
     * 来源：1-分拣，2-终端
     */
    private Integer source;

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

    public String getOpeSiteId() {
        return opeSiteId;
    }

    public void setOpeSiteId(String opeSiteId) {
        this.opeSiteId = opeSiteId;
    }

    public String getOpeSiteName() {
        return opeSiteName;
    }

    public void setOpeSiteName(String opeSiteName) {
        this.opeSiteName = opeSiteName;
    }

    public String getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(String opeTime) {
        this.opeTime = opeTime;
    }

    public List<ItmsPackageDetail> getPackageDetailList() {
        return packageDetailList;
    }

    public void setPackageDetailList(List<ItmsPackageDetail> packageDetailList) {
        this.packageDetailList = packageDetailList;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }
}
