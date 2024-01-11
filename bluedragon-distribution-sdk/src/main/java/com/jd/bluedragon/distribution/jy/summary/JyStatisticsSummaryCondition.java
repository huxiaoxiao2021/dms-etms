package com.jd.bluedragon.distribution.jy.summary;

import java.io.Serializable;
import java.util.List;

public class JyStatisticsSummaryCondition extends JyStatisticsSummaryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> businessKeyList;

    private Integer limit;

    public List<String> getBusinessKeyList() {
        return businessKeyList;
    }

    public void setBusinessKeyList(List<String> businessKeyList) {
        this.businessKeyList = businessKeyList;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}