package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;

public class TwiceExchangeCheckDto implements Serializable {

    private static final long serialVersionUID = 1L;


    /// 原运单号
    public String oldWaybillCode ;

    /// 理赔状态
    public String statusOfLP ;

    /// 物权归属
    public String goodOwner ;
    
    /// 可选退货目的地类型
    /// 
    /// 三位 000  111   第一位代表备件库 第二位代表商家 第三位代表自定义  1可选 0不可选
    /// 
      
    public String returnDestinationTypes;

    public String getOldWaybillCode() {
        return oldWaybillCode;
    }

    public void setOldWaybillCode(String oldWaybillCode) {
        this.oldWaybillCode = oldWaybillCode;
    }

    public String getStatusOfLP() {
        return statusOfLP;
    }

    public void setStatusOfLP(String statusOfLP) {
        this.statusOfLP = statusOfLP;
    }

    public String getGoodOwner() {
        return goodOwner;
    }

    public void setGoodOwner(String goodOwner) {
        this.goodOwner = goodOwner;
    }

    public String getReturnDestinationTypes() {
        return returnDestinationTypes;
    }

    public void setReturnDestinationTypes(String returnDestinationTypes) {
        this.returnDestinationTypes = returnDestinationTypes;
    }
}
