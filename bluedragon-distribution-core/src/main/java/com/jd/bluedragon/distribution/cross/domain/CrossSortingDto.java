package com.jd.bluedragon.distribution.cross.domain;

import java.util.List;

/**
 * Created by guoyongzhi on 2015/7/2.
 */
public class CrossSortingDto implements java.io.Serializable{
    private List<CrossSorting> crossSortingList;
    private Integer code;
    private String message;

    public List<CrossSorting> getCrossSortingList() {
        return crossSortingList;
    }

    public void setCrossSortingList(List<CrossSorting> crossSortingList) {
        this.crossSortingList = crossSortingList;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
