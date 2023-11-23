package com.jd.bluedragon.core.jsf.collectpackage.dto;

import java.util.List;

public class ListTaskStatisticQueryDto {
    List<StatisticsUnderTaskQueryDto> statisticsUnderTaskQueryDtoList;
    public List<StatisticsUnderTaskQueryDto> getStatisticsUnderTaskQueryDtoList() {
        return statisticsUnderTaskQueryDtoList;
    }

    public void setStatisticsUnderTaskQueryDtoList(List<StatisticsUnderTaskQueryDto> statisticsUnderTaskQueryDtoList) {
        this.statisticsUnderTaskQueryDtoList = statisticsUnderTaskQueryDtoList;
    }
}
