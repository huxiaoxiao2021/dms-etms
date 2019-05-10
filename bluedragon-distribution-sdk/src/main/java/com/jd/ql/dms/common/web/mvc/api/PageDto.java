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
    private int totalPage;
    private List<T> result;

    public PageDto() {
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRow() {
        return this.totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getResult() {
        return this.result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
