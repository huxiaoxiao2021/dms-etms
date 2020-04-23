package com.jd.bluedragon.distribution.financialForKA.domain;

import java.io.Serializable;

/**
 * @ClassName: WaybillCodeCheckCondition
 * @Description: 金融客户运单号对比校验查询条件
 * @author: hujiping
 * @date: 2019/3/7 15:37
 */
public class WaybillCodeCheckCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 条码1(待比较单号)
     * */
    private String barCodeOfOne;

    /**
     * 条码2
     * */
    private String barCodeOfTwo;

    /**
     * 操作站点
     * */
    private Integer operateSiteCode;

    /**
     * 操作站点名称
     * */
    private String operateSiteName;

    /**
     * 操作人ERP
     * */
    private String operateErp;

    public String getBarCodeOfOne() {
        return barCodeOfOne;
    }

    public void setBarCodeOfOne(String barCodeOfOne) {
        this.barCodeOfOne = barCodeOfOne;
    }

    public String getBarCodeOfTwo() {
        return barCodeOfTwo;
    }

    public void setBarCodeOfTwo(String barCodeOfTwo) {
        this.barCodeOfTwo = barCodeOfTwo;
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

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }
}
