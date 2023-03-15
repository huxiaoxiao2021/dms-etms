package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //集齐类型统计数据
 * @date
 **/
public class CollectReportStatisticsDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 集齐类型
     * CollectTypeEnum
     */
    private Integer collectType;
    /**
     * 集齐统计数量
     */
    private Integer statisticsNum;

    /**
     * 实际扫描数量
     */
    private Integer actualScanNum;
    /**
     * 下发任务扫描的运单是否存在初始化逻辑的标识
     */
    private Boolean taskExistInitFlag;

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Integer getStatisticsNum() {
        return statisticsNum;
    }

    public void setStatisticsNum(Integer statisticsNum) {
        this.statisticsNum = statisticsNum;
    }

    public Integer getActualScanNum() {
        return actualScanNum;
    }

    public void setActualScanNum(Integer actualScanNum) {
        this.actualScanNum = actualScanNum;
    }

    public Boolean getTaskExistInitFlag() {
        return taskExistInitFlag;
    }

    public void setTaskExistInitFlag(Boolean taskExistInitFlag) {
        this.taskExistInitFlag = taskExistInitFlag;
    }
}
