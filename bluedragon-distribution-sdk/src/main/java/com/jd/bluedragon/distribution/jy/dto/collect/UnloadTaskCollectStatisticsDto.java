package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //集齐类型统计数据
 * @date
 **/
public class UnloadTaskCollectStatisticsDto implements Serializable {

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
}
