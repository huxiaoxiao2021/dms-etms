package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class CrossDataResp implements Serializable {
    private static final long serialVersionUID = 885858431211333220L;
    //滑道编号列表
    List<String> crossCodeList;
    private Integer totalPage;

    public List<String> getCrossCodeList() {
        return crossCodeList;
    }

    public void setCrossCodeList(List<String> crossCodeList) {
        this.crossCodeList = crossCodeList;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
