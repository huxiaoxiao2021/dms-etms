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
}
