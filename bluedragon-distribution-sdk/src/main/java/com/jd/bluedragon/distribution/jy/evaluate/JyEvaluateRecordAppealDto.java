package com.jd.bluedragon.distribution.jy.evaluate;

import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价申诉请求对象
 * @date 2024/3/5
 */
public class JyEvaluateRecordAppealDto extends JyEvaluateRecordAppealEntity{
    /**
     * 场地编码
     */
    private Integer siteCode;
    /**
     * 图片集合
     */
    private List<String> imgUrlList;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }
}
