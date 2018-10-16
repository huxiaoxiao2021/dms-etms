package com.jd.bluedragon.distribution.performance.domain;

import java.io.Serializable;

/**
 * @ClassName: Performance
 * @Description: 履约单信息
 * @author: hujiping
 * @date: 2018/8/17 14:21
 */
public class Performance implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 履约单号 */
    private String performanceCode;
    /** PO单号 */
    private String poNo;
    /** 运单号 */
    private String waybillCode;
    /** 包裹号 */
    private String packageCode;
    /** 商品名称 */
    private String goodName;
    /** 商品数量 */
    private Integer goodNumber;
    /** 是否可以打印 */
    private Integer isCanPrint;

    public Integer getIsCanPrint() {
        return isCanPrint;
    }

    public void setIsCanPrint(Integer isCanPrint) {
        this.isCanPrint = isCanPrint;
    }

    public String getPerformanceCode() {
        return performanceCode;
    }

    public void setPerformanceCode(String performanceCode) {
        this.performanceCode = performanceCode;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Integer getGoodNumber() {
        return goodNumber;
    }

    public void setGoodNumber(Integer goodNumber) {
        this.goodNumber = goodNumber;
    }

    @Override
    public String toString() {
        return "Performance{" +
                "performanceCode='" + performanceCode + '\'' +
                ", poNo='" + poNo + '\'' +
                ", waybillCode='" + waybillCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", goodName='" + goodName + '\'' +
                ", goodNumber=" + goodNumber +
                ", isCanPrint=" + isCanPrint +
                '}';
    }
}
