package com.jd.bluedragon.distribution.globaltrade.domain;

import java.io.Serializable;

public class LoadBillConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分拣中心编号
     */
    private Integer dmsCode;

    /**
     * 仓库ID
     */
    private String warehouseId;

    /**
     * 申报海关编码。
     */
    private String ctno;

    /**
     * 申报国检编码。
     */
    private String gjno;

    /**
     * 物流企业编码。
     */
    private String tpl;

    public Integer getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(Integer dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCtno() {
        return ctno;
    }

    public void setCtno(String ctno) {
        this.ctno = ctno;
    }

    public String getGjno() {
        return gjno;
    }

    public void setGjno(String gjno) {
        this.gjno = gjno;
    }

    public String getTpl() {
        return tpl;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

}
