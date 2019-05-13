package com.jd.ql.dms.common.web.mvc.api;

import java.io.Serializable;
import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/5/9
 */
public class PageDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int currentPage = 1;
    private int pageSize = 100;
    private int totalRow;
    private List<T> result;

    public PageDto() {
    }

    public PageDto(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        if (this.currentPage < 1) {
            this.currentPage = 1;
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }

    public int getTotalRow() {
        return this.totalRow;
    }

    public void setTotalRow(int totalRow) {
        if (totalRow > 0) {
            this.totalRow = totalRow;
        }
    }

    public int getTotalPage() {
        return (int) Math.ceil((double) getTotalRow() / (double) getPageSize());
    }

    public int getOffset() {
        int page_index = getCurrentPage() - 1;
        page_index = page_index < 0 ? 0 : page_index;
        return page_index * pageSize;
    }

    /**
     * 是否还有下一页.
     */
    public boolean hasNextPage() {
        return (getCurrentPage() < getTotalPage());
    }

    public List<T> getResult() {
        return this.result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
