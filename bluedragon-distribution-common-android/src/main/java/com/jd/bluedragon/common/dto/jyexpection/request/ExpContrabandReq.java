package com.jd.bluedragon.common.dto.jyexpection.request;

import java.io.Serializable;
import java.util.List;

public class ExpContrabandReq extends ExpBaseReq {
    private static final long serialVersionUID = 4826466489545798614L;
    /**
     * 包裹号
     */
    private String barCode;
    /**
     * 修复前、包装前）图片地址
     */
    private List<String> imageUrlList;

    /**
     * 违禁品类型（1：扣减 2.航空转陆运 3.退回）
     */
    private Integer contrabandType;

    // 货物情况
    private String description;
    
    private String siteName;

    /**
     * 包裹面单图片地址
     */
    private List<String> waybillImageUrlList;

    /**
     * 货物全景照
     */
    private List<String> panoramaImageUrlList;

    /**
     * 违禁品照
     */
    private List<String> contrabandImageUrlList;

    /**
     * 一级异常原因
     */
    private String firstReasonLevelCode;

    /**
     * 二级异常原因
     */
    private String secondReasonLevelCode;

    /**
     * 三级异常原因
     */
    private String thirdReasonLevelCode;

    /**
     * 一级异常原因名称
     */
    private String firstReasonLevelName;

    /**
     * 二级异常原因名称
     */
    private String secondReasonLevelName;

    /**
     * 三级异常原因名称
     */
    private String thirdReasonLevelName;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public Integer getContrabandType() {
        return contrabandType;
    }

    public void setContrabandType(Integer contrabandType) {
        this.contrabandType = contrabandType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getFirstReasonLevelCode() {
        return firstReasonLevelCode;
    }

    public void setFirstReasonLevelCode(String firstReasonLevelCode) {
        this.firstReasonLevelCode = firstReasonLevelCode;
    }

    public String getSecondReasonLevelCode() {
        return secondReasonLevelCode;
    }

    public void setSecondReasonLevelCode(String secondReasonLevelCode) {
        this.secondReasonLevelCode = secondReasonLevelCode;
    }

    public String getThirdReasonLevelCode() {
        return thirdReasonLevelCode;
    }

    public void setThirdReasonLevelCode(String thirdReasonLevelCode) {
        this.thirdReasonLevelCode = thirdReasonLevelCode;
    }

    public String getFirstReasonLevelName() {
        return firstReasonLevelName;
    }

    public void setFirstReasonLevelName(String firstReasonLevelName) {
        this.firstReasonLevelName = firstReasonLevelName;
    }

    public String getSecondReasonLevelName() {
        return secondReasonLevelName;
    }

    public void setSecondReasonLevelName(String secondReasonLevelName) {
        this.secondReasonLevelName = secondReasonLevelName;
    }

    public String getThirdReasonLevelName() {
        return thirdReasonLevelName;
    }

    public void setThirdReasonLevelName(String thirdReasonLevelName) {
        this.thirdReasonLevelName = thirdReasonLevelName;
    }

    public List<String> getWaybillImageUrlList() {
        return waybillImageUrlList;
    }

    public void setWaybillImageUrlList(List<String> waybillImageUrlList) {
        this.waybillImageUrlList = waybillImageUrlList;
    }

    public List<String> getPanoramaImageUrlList() {
        return panoramaImageUrlList;
    }

    public void setPanoramaImageUrlList(List<String> panoramaImageUrlList) {
        this.panoramaImageUrlList = panoramaImageUrlList;
    }

    public List<String> getContrabandImageUrlList() {
        return contrabandImageUrlList;
    }

    public void setContrabandImageUrlList(List<String> contrabandImageUrlList) {
        this.contrabandImageUrlList = contrabandImageUrlList;
    }
}
