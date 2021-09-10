package com.jd.bluedragon.common.dto.working;

import java.io.Serializable;
import java.util.List;

public class WorkingPage <T> implements Serializable {
    private static final long serialVersionUID = -1939932143261575077L;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public T getSearchVo() {
        return searchVo;
    }

    public void setSearchVo(T searchVo) {
        this.searchVo = searchVo;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页显示数量
     */
    private int pageSize;
    /**
     * 总数
     */
    private long total;

    /**
     * ES scrollId
     */
    private String scrollId;

    /**
     * 查询条件
     */
    private T searchVo;

    /**
     * 数据
     */
    private List<T> data;
}
