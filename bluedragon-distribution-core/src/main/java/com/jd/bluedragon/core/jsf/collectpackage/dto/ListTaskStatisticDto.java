package com.jd.bluedragon.core.jsf.collectpackage.dto;

import java.util.List;

public class ListTaskStatisticDto {
    List<StatisticsUnderTaskDto> statisticsUnderTaskDtoList;
    public List<StatisticsUnderTaskDto> getStatisticsUnderTaskDtoList() {
        return statisticsUnderTaskDtoList;
    }

    public void setStatisticsUnderTaskDtoList(List<StatisticsUnderTaskDto> statisticsUnderTaskDtoList) {
        this.statisticsUnderTaskDtoList = statisticsUnderTaskDtoList;
    }
}
