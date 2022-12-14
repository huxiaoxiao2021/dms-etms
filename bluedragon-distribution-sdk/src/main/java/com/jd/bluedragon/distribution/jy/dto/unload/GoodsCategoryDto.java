package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
public class GoodsCategoryDto implements Serializable {
    private static final long serialVersionUID = 7431307375891856971L;
    private String type;
    private String name;
    private Integer shouldScanCount;
    private Integer haveScanCount;
    private Integer waitScanCount;
    private Integer moreScanTotalCount;

    /**
     * 排序
     */
    private Integer order;


    public static class OrderComparator implements java.util.Comparator<GoodsCategoryDto> {
        @Override
        public int compare(GoodsCategoryDto o1, GoodsCategoryDto o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Integer shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Integer getHaveScanCount() {
        return haveScanCount;
    }

    public void setHaveScanCount(Integer haveScanCount) {
        this.haveScanCount = haveScanCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }

    public Integer getMoreScanTotalCount() {
        return moreScanTotalCount;
    }

    public void setMoreScanTotalCount(Integer moreScanTotalCount) {
        this.moreScanTotalCount = moreScanTotalCount;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
