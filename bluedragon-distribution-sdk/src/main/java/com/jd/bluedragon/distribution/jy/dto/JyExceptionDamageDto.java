package com.jd.bluedragon.distribution.jy.dto;

import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;

import java.util.List;

public class JyExceptionDamageDto extends JyExceptionDamageEntity {
    private String damageTypeName;

    private String repairTypeName;

    private String feedBackTypeName;

    /**
     * 修复前、包装前）图片地址
     */
    private List<String> actualImageUrlList;
    /**
     * （修复后、包装后）图片地址
     */
    private List<String> dealImageUrlList;

    public String getDamageTypeName() {
        return damageTypeName;
    }

    public void setDamageTypeName(String damageTypeName) {
        this.damageTypeName = damageTypeName;
    }

    public String getRepairTypeName() {
        return repairTypeName;
    }

    public void setRepairTypeName(String repairTypeName) {
        this.repairTypeName = repairTypeName;
    }

    public String getFeedBackTypeName() {
        return feedBackTypeName;
    }

    public void setFeedBackTypeName(String feedBackTypeName) {
        this.feedBackTypeName = feedBackTypeName;
    }

    public List<String> getActualImageUrlList() {
        return actualImageUrlList;
    }

    public void setActualImageUrlList(List<String> actualImageUrlList) {
        this.actualImageUrlList = actualImageUrlList;
    }

    public List<String> getDealImageUrlList() {
        return dealImageUrlList;
    }

    public void setDealImageUrlList(List<String> dealImageUrlList) {
        this.dealImageUrlList = dealImageUrlList;
    }
}
