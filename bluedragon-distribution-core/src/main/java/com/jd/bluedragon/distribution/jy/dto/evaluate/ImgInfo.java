package com.jd.bluedragon.distribution.jy.dto.evaluate;

import java.util.List;

public class ImgInfo {
    /**
     * 装车   1
     * 卸车前 2
     * 卸车中 3
     */
    private Integer imgType;
    /**
     * 图片url
     */
    private List<String> imgURLs;

    public Integer getImgType() {
        return imgType;
    }

    public void setImgType(Integer imgType) {
        this.imgType = imgType;
    }

    public List<String> getImgURLs() {
        return imgURLs;
    }

    public void setImgURLs(List<String> imgURLs) {
        this.imgURLs = imgURLs;
    }
}
