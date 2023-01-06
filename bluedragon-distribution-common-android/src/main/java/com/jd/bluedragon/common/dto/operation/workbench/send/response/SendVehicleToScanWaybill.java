package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;


public class SendVehicleToScanWaybill implements Serializable {

    private static final long serialVersionUID = -161979898323473867L;

    /**
     * 包裹号
     */
    private String waybillCode;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 单号标签集合
     */
    private List<LabelOption> tags;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<LabelOption> getTags() {
        return tags;
    }

    public void setTags(List<LabelOption> tags) {
        this.tags = tags;
    }
}
