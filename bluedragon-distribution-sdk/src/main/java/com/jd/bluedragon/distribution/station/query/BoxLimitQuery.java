package com.jd.bluedragon.distribution.station.query;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

public class BoxLimitQuery  extends BasePagerCondition {

    /**
     * 机构名称
     */
    private String siteName;
    /**
     * 机构ID
     */
    private Integer siteId;
    /**
     * 建箱包裹上限
     */
    private Integer limitNum;

    /**
     * 配置类型(1:通用配置 2：场地建箱配置)
     */
    private Integer configType =2;

    /**
     * 箱号类型
     *   1-BC（正向普通） 2-TC（退货普通） 3-GC（取件普通） 4-FC（返调度再投普通） 5-BS（正向奢侈品） 6-TS（退货奢侈品） 7-GS（取件奢侈品）
     *   8-FS（返调度再投奢侈品） 9-FC（签单返还） 10-ZC（上门接货） 11-ZC（商家售后） 12-BX（正向虚拟） 13-TW（逆向内配） 14-WJ(文件信封)
     */
    private String boxNumberType;

    private Integer pageSize;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public String getBoxNumberType() {
        return boxNumberType;
    }

    public void setBoxNumberType(String boxNumberType) {
        this.boxNumberType = boxNumberType;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
