package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/14 16:31
 */
public class SpotCheckOfPackageDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号/包裹号
     * */
    private String billCode;
    /**
     * 长
     * */
    private Double length;
    /**
     * 宽
     * */
    private Double width;
    /**
     * 高
     * */
    private Double height;
    /**
     * 重量
     * */
    private Double weight;
    /**
     * 包裹维度超标图片
     * */
    private List<Map<String,String>> imgList;

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<Map<String, String>> getImgList() {
        return imgList;
    }

    public void setImgList(List<Map<String, String>> imgList) {
        this.imgList = imgList;
    }
}
