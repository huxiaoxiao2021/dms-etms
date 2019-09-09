package com.jd.bluedragon.distribution.auto.domain;

import java.io.Serializable;
import java.util.*;

/**
 * 龙门架上传数据
 * Created by wangtingwei on 2016/3/10.
 */
public class UploadData implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer MAX_BARCODE_LENGTH = 32;

    public static final Integer NOT_PACKAGECODE_BOXCDOE = 4002;

    public static final String NOT_PACKAGECODE_BOXCDOE_MESSAGE = "条码非法，必须为包裹号或箱号";

    public static final Integer BARCODE_NULL_OR_EMPTY_CODE = 4001;

    public static final String BARCODE_NULL_OR_EMPTY_MESSAGE = "条码为空";

    /**
     * 条码
     */
    private String barCode;

    /**
     * 高
     */
    private Float height;

    /**
     * 宽
     */
    private Float width;

    /**
     * 长
     */
    private Float length;

    /**
     * 重量
     */
    private Float weight;

    /**
     * 龙门架注册号/分拣机器代码
     */
    private String registerNo;

    /**
     * 扫描时间
     */
    private Date scannerTime;

    /**
     * 请求来源，1-龙门架，2-自动分拣机【可选，为空默认为龙门架】
     */
    private Integer source;

    /**
     * 包裹号【可选，来源为自动分拣机时使用】
     */
    private String packageCode;

    /**
     * 箱号【可选，来源为自动分拣机时必填】
     */
    private String boxCode;

    /**
     * 滑槽号
     */
    private String chuteCode;

    /**
     * 分拣中心ID【可选，来源为自动分拣机时必填】
     */
    private Integer distributeId;

//    /**
//     * 分拣中心名称【可选，来源为自动分拣机时必填】
//     */
//    private String distributeName;

    /**
     * 分拣目的地编号【可选，来源为自动分拣机时必填】
     */
    private Integer boxSiteCode;

    /**
     * 发货目的地编号【可选，来源为自动分拣机时必填】
     */
    private Integer sendSiteCode;

//    /**
//     * 发货目的地名称【可选，来源为自动分拣机时必填】
//     */
//    private String sendSiteName;

    /**
     * 操作人ID【可选，来源为自动分拣机时必填】
     */
    private Integer operatorId;

    /**
     * 操作人名称【可选，来源为自动分拣机时必填】
     */
    private String operatorName;

    public Date getScannerTime() {
        return scannerTime;
    }

    public void setScannerTime(Date scannerTime) {
        this.scannerTime = scannerTime;
    }

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getChuteCode() {
        return chuteCode;
    }

    public void setChuteCode(String chuteCode) {
        this.chuteCode = chuteCode;
    }

    public Integer getDistributeId() {
        return distributeId;
    }

    public void setDistributeId(Integer distributeId) {
        this.distributeId = distributeId;
    }

//    public String getDistributeName() {
//        return distributeName;
//    }
//
//    public void setDistributeName(String distributeName) {
//        this.distributeName = distributeName;
//    }


    public Integer getBoxSiteCode() {
        return boxSiteCode;
    }

    public void setBoxSiteCode(Integer boxSiteCode) {
        this.boxSiteCode = boxSiteCode;
    }

    public Integer getSendSiteCode() {
        return sendSiteCode;
    }

    public void setSendSiteCode(Integer sendSiteCode) {
        this.sendSiteCode = sendSiteCode;
    }

//    public String getSendSiteName() {
//        return sendSiteName;
//    }
//
//    public void setSendSiteName(String sendSiteName) {
//        this.sendSiteName = sendSiteName;
//    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

}
