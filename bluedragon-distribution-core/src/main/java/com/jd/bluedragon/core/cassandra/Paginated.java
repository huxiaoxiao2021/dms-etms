package com.jd.bluedragon.core.cassandra;

import com.datastax.driver.core.PagingState;

import java.util.List;

/**
 * cyk
 */
public class Paginated<E> {
    private List<E>  pagedObjects;
    private int pageSize;
    private PagingState pagingState;


    @Override
    public String toString() {
        return "Paginated{" +
                "pagedObjects=" +  pagedObjects +
                ", pageSize=" + pageSize +
                ", pagingState=" + pagingState +
                '}';
    }

    public List<E> getPagedObjects() {
        return pagedObjects;
    }

    public void setPagedObjects(List<E> pagedObjects) {
        this.pagedObjects = pagedObjects;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public PagingState getPagingState() {
        return pagingState;
    }

    public void setPagingState(PagingState pagingState) {
        this.pagingState = pagingState;
    }
}
