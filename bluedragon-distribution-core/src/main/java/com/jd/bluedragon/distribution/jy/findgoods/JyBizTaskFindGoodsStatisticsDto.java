package com.jd.bluedragon.distribution.jy.findgoods;

/**
 * @Author zhengchengfa
 * @Date 2023/7/17 13:42
 * @Description
 */
public class JyBizTaskFindGoodsStatisticsDto {

    /**
     * 总任务数
     */
    private Integer totalTaskNum;

    /**
     * 总任务数
     */
    private Integer totalPackageNum;


    public Integer getTotalTaskNum() {
        return totalTaskNum;
    }

    public void setTotalTaskNum(Integer totalTaskNum) {
        this.totalTaskNum = totalTaskNum;
    }

    public Integer getTotalPackageNum() {
        return totalPackageNum;
    }

    public void setTotalPackageNum(Integer totalPackageNum) {
        this.totalPackageNum = totalPackageNum;
    }
}
