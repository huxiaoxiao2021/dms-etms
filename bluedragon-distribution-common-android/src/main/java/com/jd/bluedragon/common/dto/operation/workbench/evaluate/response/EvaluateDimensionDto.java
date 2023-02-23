package com.jd.bluedragon.common.dto.operation.workbench.evaluate.response;

import java.io.Serializable;
import java.util.List;

public class EvaluateDimensionDto implements Serializable {

    private static final long serialVersionUID = 8475029193415431666L;

    /**
     * 评价维度编码
     */
    private Integer dimensionCode;

    /**
     * 评价维度名称
     */
    private String dimensionName;

    /**
     * 评价维度状态:1-重点关注
     */
    private Integer status;

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

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
