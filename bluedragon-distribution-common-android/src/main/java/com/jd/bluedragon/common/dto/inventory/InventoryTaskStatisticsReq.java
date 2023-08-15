package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;


/**
 * 找货任务
 */
public class InventoryTaskStatisticsReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    private Integer statisticsDays;

    public Integer getStatisticsDays() {
        return statisticsDays;
    }

    public void setStatisticsDays(Integer statisticsDays) {
        this.statisticsDays = statisticsDays;
    }
}
