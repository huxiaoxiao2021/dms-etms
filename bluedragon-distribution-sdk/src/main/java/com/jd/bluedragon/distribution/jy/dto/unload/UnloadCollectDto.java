package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //卸车集齐返回
 * @date
 **/
public class UnloadCollectDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;

    private String waybillCode;
    /**
     * 集齐方式
     * CollectTypeEnum
     */
    private Integer collectType;
    /**
     * 集齐统计数据
     */
    private Integer collectStatisticsNum;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Integer getCollectStatisticsNum() {
        return collectStatisticsNum;
    }

    public void setCollectStatisticsNum(Integer collectStatisticsNum) {
        this.collectStatisticsNum = collectStatisticsNum;
    }
}
