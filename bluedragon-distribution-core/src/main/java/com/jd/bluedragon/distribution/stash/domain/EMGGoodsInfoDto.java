package com.jd.bluedragon.distribution.stash.domain;

/**
 * <P>
 *     收纳暂存项目，EMG条码相关的信息
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/6/10
 */
public class EMGGoodsInfoDto {

    /**
     * 商家订单号
     */
    private String businessOrderCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 事业部商品条码
     */
    private String goodsNo;

    /**
     * ISV主商品编码（EMG唯一码）
     */
    private String emgCode;

    /**
     * 货主编号
     */
    private String deptNo;

    /**
     * 货主名称
     */
    private String deptName;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号 截止到2019-6-10 18:06:59，一个EMGCode对应一个包裹号
     */
    private String packageCode;

    public String getBusinessOrderCode() {
        return businessOrderCode;
    }

    public void setBusinessOrderCode(String businessOrderCode) {
        this.businessOrderCode = businessOrderCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getEmgCode() {
        return emgCode;
    }

    public void setEmgCode(String emgCode) {
        this.emgCode = emgCode;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
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
}
