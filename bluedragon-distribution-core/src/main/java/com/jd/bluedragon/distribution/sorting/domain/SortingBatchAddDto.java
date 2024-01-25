package com.jd.bluedragon.distribution.sorting.domain;

import java.util.List;

public class SortingBatchAddDto {
    private List<Sorting> sortingList;
    private Long id;

    public List<Sorting> getSortingList() {
        return sortingList;
    }

    public void setSortingList(List<Sorting> sortingList) {
        this.sortingList = sortingList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
