package com.jd.bluedragon.common.dto.predict.model;


import java.util.ArrayList;
import java.util.List;

public class InspectedNotSendBarCode {

    String barCode;
    List<BizLineType> bizLineTypes=new ArrayList<BizLineType>();
    String productType;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<BizLineType> getBizLineTypes() {
        return bizLineTypes;
    }

    public void setBizLineTypes(List<BizLineType> bizLineTypes) {
        this.bizLineTypes = bizLineTypes;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
