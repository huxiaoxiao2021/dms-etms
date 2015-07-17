package com.jd.bluedragon.distribution.cross.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * Created by yanghongqiang on 2015/7/8.
 */
public class CrossSortingResponse extends JdResponse {

    private static final long serialVersionUID = 6421643159029953993L;

    List<CrossSorting> data;

    public CrossSortingResponse() {
        super();
    }

    public CrossSortingResponse(Integer code, String message) {
        super(code, message);
    }

    public List<CrossSorting> getData(){
        return data;
    }

    public void setData(List<CrossSorting> batchSends){
        this.data=batchSends;
    }
}
