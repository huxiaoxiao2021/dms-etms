package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class TableTrolleyResp implements Serializable {
    private static final long serialVersionUID = 7575304026469453741L;
    private List<TableTrolleyDto> tableTrolleyDtoList;
    private Integer totalPage;

    public List<TableTrolleyDto> getTableTrolleyDtoList() {
        return tableTrolleyDtoList;
    }

    public void setTableTrolleyDtoList(List<TableTrolleyDto> tableTrolleyDtoList) {
        this.tableTrolleyDtoList = tableTrolleyDtoList;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
