package com.jd.bluedragon.distribution.globaltrade.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author dudong
 * @date 2015/8/25
 */
public class PreLoadBill implements Serializable{
    private static final long serialVersionUID = -5571202720781495875L;

    private String loadId;
    private String warehouseId;
    private String packgeAmount;
    private String ctno;
    private String gjno;
    private String tpl;
    private String truckNo;
    private String genTime;
    private Integer orderCount;
    private List<LoadBill> orderList;

    public String getLoadId() {
        return loadId;
    }

    public void setLoadId(String loadId) {
        this.loadId = loadId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getPackgeAmount() {
        return packgeAmount;
    }

    public void setPackgeAmount(String packgeAmount) {
        this.packgeAmount = packgeAmount;
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

    public String getTruckNo() {
        return truckNo;
    }

    public void setTruckNo(String truckNo) {
        this.truckNo = truckNo;
    }

    public String getGenTime() {
        return genTime;
    }

    public void setGenTime(String genTime) {
        this.genTime = genTime;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public List<LoadBill> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<LoadBill> orderList) {
        this.orderList = orderList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreLoadBill that = (PreLoadBill) o;

        if (loadId != null ? !loadId.equals(that.loadId) : that.loadId != null) return false;
        if (warehouseId != null ? !warehouseId.equals(that.warehouseId) : that.warehouseId != null) return false;
        if (packgeAmount != null ? !packgeAmount.equals(that.packgeAmount) : that.packgeAmount != null) return false;
        if (ctno != null ? !ctno.equals(that.ctno) : that.ctno != null) return false;
        if (gjno != null ? !gjno.equals(that.gjno) : that.gjno != null) return false;
        if (tpl != null ? !tpl.equals(that.tpl) : that.tpl != null) return false;
        if (truckNo != null ? !truckNo.equals(that.truckNo) : that.truckNo != null) return false;
        return !(genTime != null ? !genTime.equals(that.genTime) : that.genTime != null);

    }

    @Override
    public int hashCode() {
        int result = loadId != null ? loadId.hashCode() : 0;
        result = 31 * result + (warehouseId != null ? warehouseId.hashCode() : 0);
        result = 31 * result + (packgeAmount != null ? packgeAmount.hashCode() : 0);
        result = 31 * result + (ctno != null ? ctno.hashCode() : 0);
        result = 31 * result + (gjno != null ? gjno.hashCode() : 0);
        result = 31 * result + (tpl != null ? tpl.hashCode() : 0);
        result = 31 * result + (truckNo != null ? truckNo.hashCode() : 0);
        result = 31 * result + (genTime != null ? genTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PreLoadBill{" +
                "genTime=" + genTime +
                ", loadId='" + loadId + '\'' +
                ", warehouseId='" + warehouseId + '\'' +
                ", packgeAmount='" + packgeAmount + '\'' +
                ", ctno='" + ctno + '\'' +
                ", gjno='" + gjno + '\'' +
                ", tpl='" + tpl + '\'' +
                ", truckNo='" + truckNo + '\'' +
                '}';
    }
}
