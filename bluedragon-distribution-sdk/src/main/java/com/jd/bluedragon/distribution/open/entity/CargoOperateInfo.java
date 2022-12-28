package com.jd.bluedragon.distribution.open.entity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: CargoOperateInfo
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 10:09
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class CargoOperateInfo {

    /**
     * 操作单号
     */
    private String barcode;

    /**
     * 操作单号类型：1-京东包裹号；2-京东运单号；3-京东箱号
     * @link com.jd.bluedragon.dms.utils.BarCodeType
     */
    private String barcodeType;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
