package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.select.SelectOption;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UnloadScanBarCode
 * @Description
 * @Author wyh
 * @Date 2022/3/31 23:04
 **/
public class UnloadScanBarCode implements Serializable {

    private static final long serialVersionUID = -1619798983105573867L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 单号标签集合
     */
    private List<SelectOption> tags;

    /**
     * 扫描类型 待扫，已扫，非本场地多扫，本场地多扫
     */
    private String scanType;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<SelectOption> getTags() {
        return tags;
    }

    public void setTags(List<SelectOption> tags) {
        this.tags = tags;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }
}
