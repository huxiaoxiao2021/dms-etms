package com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/28 21:11
 */
public class FirstSiteVo implements Serializable {
    /**
     * 始发站点编码
     */
    private Integer firstSiteCode;

    /**
     * 始发站点名称
     */
    private String  firstSiteName;

    public Integer getFirstSiteCode() {
        return firstSiteCode;
    }

    public void setFirstSiteCode(Integer firstSiteCode) {
        this.firstSiteCode = firstSiteCode;
    }

    public String getFirstSiteName() {
        return firstSiteName;
    }

    public void setFirstSiteName(String firstSiteName) {
        this.firstSiteName = firstSiteName;
    }
}
    
