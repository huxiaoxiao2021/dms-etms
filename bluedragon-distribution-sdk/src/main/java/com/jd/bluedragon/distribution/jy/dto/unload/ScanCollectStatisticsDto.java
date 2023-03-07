package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

/**
 * 扫描统计数据
 */
public class ScanCollectStatisticsDto implements Serializable {
    private static final long serialVersionUID = 8179121445309123824L;
    /**
     * 集齐类型
     */
    private Integer collectType;
    /**
     * 不齐运单数
     */
    private Integer waybillBuQiNum;


    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public Integer getWaybillBuQiNum() {
        return waybillBuQiNum;
    }

    public void setWaybillBuQiNum(Integer waybillBuQiNum) {
        this.waybillBuQiNum = waybillBuQiNum;
    }
}
