package com.jd.bluedragon.distribution.middleend.sorting.domain;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;

import java.io.Serializable;

public class SortingObjectExtend implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 始发站点
     */
    private DmsCustomSite createSite;

    /**
     * 目的站点
     */
    private DmsCustomSite receiveSite;

    /**
     * 分拣的sorting对象
     */
    private Sorting dmsSorting;

    /**
     * 中台定义的分拣对象
     */
    private SortingObject middleEndSorting;

    /**
     * 大运单拆分-第几页
     */
    private Integer packagePageIndex;

    /**
     * 大运单拆分-每页条数
     */
    private Integer packagePageSize;

    public DmsCustomSite getCreateSite() {
        return createSite;
    }

    public void setCreateSite(DmsCustomSite createSite) {
        this.createSite = createSite;
    }

    public DmsCustomSite getReceiveSite() {
        return receiveSite;
    }

    public void setReceiveSite(DmsCustomSite receiveSite) {
        this.receiveSite = receiveSite;
    }

    public Sorting getDmsSorting() {
        return dmsSorting;
    }

    public void setDmsSorting(Sorting dmsSorting) {
        this.dmsSorting = dmsSorting;
    }

    public Integer getPackagePageIndex() {
        return packagePageIndex;
    }

    public void setPackagePageIndex(Integer packagePageIndex) {
        this.packagePageIndex = packagePageIndex;
    }

    public Integer getPackagePageSize() {
        return packagePageSize;
    }

    public void setPackagePageSize(Integer packagePageSize) {
        this.packagePageSize = packagePageSize;
    }

    public SortingObject getMiddleEndSorting() {
        return middleEndSorting;
    }

    public void setMiddleEndSorting(SortingObject middleEndSorting) {
        this.middleEndSorting = middleEndSorting;
    }
}
