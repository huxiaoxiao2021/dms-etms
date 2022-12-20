package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendVehicleToScanPackage
 * @Description
 * @Author chenyaguo
 * @Date 2022/3/31 23:04
 **/
public class SendVehicleToScanPackage implements Serializable {

    private static final long serialVersionUID = -161979898323473867L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 单号标签集合
     */
    private List<LabelOption> tags;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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
