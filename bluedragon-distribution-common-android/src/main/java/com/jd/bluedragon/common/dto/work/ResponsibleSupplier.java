package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

public class ResponsibleSupplier implements Serializable {
    /**
     *  外包商Id
     */
    private String supplierId;
    /**
     * 外包商名称
     */
    private String supplierName;

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
