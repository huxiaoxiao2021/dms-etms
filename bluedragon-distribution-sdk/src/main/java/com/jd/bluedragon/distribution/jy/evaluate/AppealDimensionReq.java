package com.jd.bluedragon.distribution.jy.evaluate;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价的不满意项
 * @date 2024/3/5
 */
public class AppealDimensionReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 不满意项编码
     */
    private Integer dimensionCode;
    /**
     *图片集合
     */
    private List<String> imgUrlList;
    /**
     *  申诉理由
     */
    private String reasons;

    public Integer getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(Integer dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }
}
