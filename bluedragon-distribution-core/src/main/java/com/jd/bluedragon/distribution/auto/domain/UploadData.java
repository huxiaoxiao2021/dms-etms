package com.jd.bluedragon.distribution.auto.domain;

import java.io.Serializable;
import java.util.*;
/**
 * 龙门架上传数据
 * Created by wangtingwei on 2016/3/10.
 */
public class UploadData implements Serializable {
    private static final long serialVersionUID=1L;

    public static final Integer MAX_BARCODE_LENGTH=32;
    public static final Integer MAX_BARCODE_LENGTH_CODE=4003;
    public static final String  MAX_BARCODE_LENGTH_MESSAGE="条码长度超32字符";

    public static final Integer BARCODE_NULL_OR_EMPTY_CODE=4001;
    public static final String  BARCODE_NULL_OR_EMPTY_MESSAGE="条码为空";
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

    private Float weight;

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    /**
     * 龙门架注册号
     */
    private String registerNo;

    /**
     * 扫描时间
     */
    private Date scannerTime;

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
}
