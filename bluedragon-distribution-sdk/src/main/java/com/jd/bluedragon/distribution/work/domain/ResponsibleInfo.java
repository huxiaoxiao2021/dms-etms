package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 暴力分拣任务责任信息
 */
public class ResponsibleInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工种：1-正式工 2-外包工 3-临时工
     */
    private Integer workType;
    /**
     * 姓名
     */
    private String name;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * erp : 自营员工
     */
    private String erp;
    //外包工外包商
    private ResponsibleSupplier supplier;
    /**
     * 外包工 外包商选项
     */
    private List<ResponsibleSupplier> supplierList;
    
    /**
     * 网格组长
     * @return
     */
    private String gridOwnerErp;
    
    private String gridOwnerName;
    
    public Integer getWorkType() {
        return workType;
    }

    public void setWorkType(Integer workType) {
        this.workType = workType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getGridOwnerErp() {
        return gridOwnerErp;
    }

    public void setGridOwnerErp(String gridOwnerErp) {
        this.gridOwnerErp = gridOwnerErp;
    }

    public String getGridOwnerName() {
        return gridOwnerName;
    }

    public void setGridOwnerName(String gridOwnerName) {
        this.gridOwnerName = gridOwnerName;
    }

    public ResponsibleSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(ResponsibleSupplier supplier) {
        this.supplier = supplier;
    }

    public List<ResponsibleSupplier> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<ResponsibleSupplier> supplierList) {
        this.supplierList = supplierList;
    }
}
