package com.jd.bluedragon.distribution.sorting.domain;

import com.jd.bluedragon.distribution.api.request.PackageRequest;

public class SortingQuery extends Sorting{
    private int pageNo;
    private int pageSize;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
