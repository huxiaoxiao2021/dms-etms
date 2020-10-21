package com.jd.bluedragon.distribution.auto.domain;

import java.io.Serializable;

/**
 * 自动分拣机上传包裹数据
 * Created by wangtingwei on 2014/10/17.
 */
public class UploadedPackage implements Serializable{

    //包裹号
    private	String barcode;
    //分拣中心编号
    private	Integer sortCenterNo;
    //分拣中心名称
    private String sortCenterName;
    //目的地
    private String logicDest;
    //分拣时间
    private	String timeStamp;
    //滑道号
    private	String chute;
    //重量
    private	Double weight;
    //体积
    private	Double measures;
    //交接人
    private Integer operatorID;
    //交接人名字
    private String operatorName;
    //交接人erp账号
    private String erpAccount;

    private Integer bizSource;

    public Integer getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(Integer operatorID) {
        this.operatorID = operatorID;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getErpAccount() {
        return erpAccount;
    }

    public void setErpAccount(String erpAccount) {
        this.erpAccount = erpAccount;
    }

    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getSortCenterNo() {
        return sortCenterNo;
    }
    public void setSortCenterNo(Integer sortCenterNo) {
        this.sortCenterNo = sortCenterNo;
    }
    public String getLogicDest() {
        return logicDest;
    }
    public void setLogicDest(String logicDest) {
        this.logicDest = logicDest;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getChute() {
        return chute;
    }
    public void setChute(String chute) {
        this.chute = chute;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public Double getMeasures() {
        return measures;
    }
    public void setMeasures(Double measures) {
        this.measures = measures;
    }

    public String getSortCenterName() {
        return sortCenterName;
    }

    public void setSortCenterName(String sortCenterName) {
        this.sortCenterName = sortCenterName;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

}
