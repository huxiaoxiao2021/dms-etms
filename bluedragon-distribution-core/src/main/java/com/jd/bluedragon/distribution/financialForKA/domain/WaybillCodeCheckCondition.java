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
     * 条码1
     * */
    private String barCodeOfOne;

    /**
     * 条码2
     * */
    private String barCodeOfTwo;

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
}
