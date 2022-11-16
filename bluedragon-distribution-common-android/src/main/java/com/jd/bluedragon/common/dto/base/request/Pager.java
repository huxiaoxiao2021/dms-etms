package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;
import java.util.List;

public class Pager<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 每页显示数量
     */
    private Integer pageSize;
    /**
     * 总数
     */
    private Long total;

    /**
     * 查询条件
     */
    private T searchVo;

    /**
     * 数据
     */
    private List<T> data;

    public Pager() {
    }

    public Pager(Integer pageNo, Integer pageSize, Long total) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public T getSearchVo() {
        return searchVo;
    }

    public void setSearchVo(T searchVo) {
        this.searchVo = searchVo;
    }
}
