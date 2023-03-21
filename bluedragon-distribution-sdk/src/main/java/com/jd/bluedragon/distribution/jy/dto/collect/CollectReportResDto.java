package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //
 * @date
 **/
public class CollectReportResDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 集齐维度
     * CollectSiteTypeEnum
     */
    private Integer collectDimension;
    /**
     * 集齐类型
     * CollectTypeEnum
     */
    private Integer collectType;
    /**
     * 是否自建任务标识： true
     */
    private Boolean manualCreateTaskFlag;
    /**
     * 集齐类型对应统计数据
     */
    private List<CollectReportStatisticsDto> collectReportStatisticsDtoList;
    /**
     * 运单集齐报表统计数据
     */
    private List<CollectReportDto> collectReportDtoList;

    /**
     * 时间戳，类似token 用于比较是否最新数据
     */
    private Long timeStamp;


    public Integer getCollectDimension() {
        return collectDimension;
    }

    public void setCollectDimension(Integer collectDimension) {
        this.collectDimension = collectDimension;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Boolean getManualCreateTaskFlag() {
        return manualCreateTaskFlag;
    }

    public void setManualCreateTaskFlag(Boolean manualCreateTaskFlag) {
        this.manualCreateTaskFlag = manualCreateTaskFlag;
    }

    public List<CollectReportStatisticsDto> getCollectReportStatisticsDtoList() {
        return collectReportStatisticsDtoList;
    }

    public void setCollectReportStatisticsDtoList(List<CollectReportStatisticsDto> collectReportStatisticsDtoList) {
        this.collectReportStatisticsDtoList = collectReportStatisticsDtoList;
    }

    public List<CollectReportDto> getCollectReportDtoList() {
        return collectReportDtoList;
    }
    public void setCollectReportDtoList(List<CollectReportDto> collectReportDtoList) {
        this.collectReportDtoList = collectReportDtoList;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
