package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class CollectReportResDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 集齐维度
     * com.jd.tys.pda.api.pack.dto.jycommon.TysCollectDimensionEnum
     */
    private String collectDimension;
    /**
     * 集齐类型
     * com.jd.tys.pda.api.pack.dto.jycommon.TysCollectTypeEnum
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


    public String getCollectDimension() {
        return collectDimension;
    }

    public void setCollectDimension(String collectDimension) {
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
