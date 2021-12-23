package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import java.io.Serializable;

/**
 * 弃件扫描完成统计结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-07 00:08:51 周二
 */
public class DiscardedPackageFinishStatisticsDto implements Serializable {
    private static final long serialVersionUID = -5999709069296049069L;

    private int finishCount;

    private int unFinishCount;

    public DiscardedPackageFinishStatisticsDto() {
    }

    public int getFinishCount() {
        return finishCount;
    }

    public DiscardedPackageFinishStatisticsDto setFinishCount(int finishCount) {
        this.finishCount = finishCount;
        return this;
    }

    public int getUnFinishCount() {
        return unFinishCount;
    }

    public DiscardedPackageFinishStatisticsDto setUnFinishCount(int unFinishCount) {
        this.unFinishCount = unFinishCount;
        return this;
    }

    @Override
    public String toString() {
        return "DiscardedPackageFinishStatisticsDto{" +
                "finishCount=" + finishCount +
                ", unFinishCount=" + unFinishCount +
                '}';
    }
}
