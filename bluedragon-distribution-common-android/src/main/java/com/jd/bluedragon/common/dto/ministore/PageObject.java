package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;
import java.util.List;

public class PageObject<T> implements Serializable {

  private static final long serialVersionUID = -3806764885999501760L;

  private Integer pageNo;
  private Integer pageSize;
  private Integer offset;
  private Long totalElements;
  private Integer totalPages;
  private List<T> data;

  public static class Builder {

    PageObject pageObject = new PageObject();

    public Builder data(List data) {
      if (data != null) {
        pageObject.data = data;
      }
      return this;
    }

    public Builder pageNo(Integer pageNo) {
      if (pageNo != null) {
        pageObject.pageNo = pageNo;
      }
      return this;
    }

    public Builder pageSize(Integer pageSize) {
      if (pageSize != null) {
        pageObject.pageSize = pageSize;
      }
      return this;
    }

    public Builder offset(Integer offset) {
      if (offset != null) {
        pageObject.offset = offset;
      }
      return this;
    }

    public Builder totalElements(Long totalElements) {
      if (totalElements != null) {
        pageObject.totalElements = totalElements;
      }
      return this;
    }

    public Builder totalPages(Integer totalPages) {
      if (totalPages != null) {
        pageObject.totalPages = totalPages;
      }
      return this;
    }

    public PageObject build() {
      return pageObject;
    }
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

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }
}
