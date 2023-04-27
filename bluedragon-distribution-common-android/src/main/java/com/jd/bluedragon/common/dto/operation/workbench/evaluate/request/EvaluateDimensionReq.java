package com.jd.bluedragon.common.dto.operation.workbench.evaluate.request;

import java.io.Serializable;
import java.util.List;

public class EvaluateDimensionReq implements Serializable {

    private static final long serialVersionUID = 8475029193415431666L;

    /**
     * 评价维度编码
     */
    private Integer dimensionCode;

    /**
     * 图片列表
     */
    private List<String> imgUrlList;

    /**
     * 备注
     */
    private String remark;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
