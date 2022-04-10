package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;
import java.util.List;

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
    private List<LabelOption> tags;

    /**
     * 扫描类型 待扫，已扫，非本场地多扫，本场地多扫
     */
    private Integer scanType;

    private String scanTypeDesc;

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

    public List<LabelOption> getTags() {
        return tags;
    }

    public void setTags(List<LabelOption> tags) {
        this.tags = tags;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public String getScanTypeDesc() {
        return scanTypeDesc;
    }

    public void setScanTypeDesc(String scanTypeDesc) {
        this.scanTypeDesc = scanTypeDesc;
    }
}
