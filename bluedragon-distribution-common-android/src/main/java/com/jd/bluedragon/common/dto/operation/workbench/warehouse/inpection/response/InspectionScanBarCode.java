package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * 拦截包裹
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-09 14:19:52 周日
 */
public class InspectionScanBarCode implements Serializable {

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
