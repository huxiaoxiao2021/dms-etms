package com.jd.bluedragon.distribution.jy.comboard;

import java.util.List;

/**
 * JyGroupSortCrossDetailEntity DB原生属性对象
 * 当前类属性为后期加工属性
 */
public class JyGroupSortCrossDetailEntityQueryDto extends JyGroupSortCrossDetailEntity {

    /**
     * 流向List
     */
    private List<Long> endSiteIdList;

    /**
     * 数量限制
     */
    private Integer limit;

    public List<Long> getEndSiteIdList() {
        return endSiteIdList;
    }

    public void setEndSiteIdList(List<Long> endSiteIdList) {
        this.endSiteIdList = endSiteIdList;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
