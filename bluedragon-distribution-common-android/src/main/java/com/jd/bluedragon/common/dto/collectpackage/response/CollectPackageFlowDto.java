package com.jd.bluedragon.common.dto.collectpackage.response;

import java.util.List;

/**
 * @author liwenji
 * @description 流向信息
 * @date 2023-10-12 17:56
 */
public class CollectPackageFlowDto {

    private Long endSiteId;

    private String endSiteName;

    /**
     * 该流向下集包数量
     */
    private Integer count;

    /**
     * 包裹数量
     */
    private Integer packageCount;

    /**
     * 箱号数量
     */
    private Integer boxCount;

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getBoxCount() {
        return boxCount;
    }

    public void setBoxCount(Integer boxCount) {
        this.boxCount = boxCount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }


    List<CollectPackageDto> collectPackageDtoList;

    public List<CollectPackageDto> getCollectPackageDtoList() {
        return collectPackageDtoList;
    }

    public void setCollectPackageDtoList(List<CollectPackageDto> collectPackageDtoList) {
        this.collectPackageDtoList = collectPackageDtoList;
    }
}
