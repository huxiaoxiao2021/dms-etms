package com.jd.bluedragon.distribution.jy.dto.common;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/11 18:22
 * @Description
 */
public class BoxNextSiteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String boxCode;
    //箱号流向
    private Integer nextSiteId;
    private String nextSiteName;

    /**
     * 箱号确认流向场地的key[包裹号]
     */
    private String boxConfirmNextSiteKey;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public String getBoxConfirmNextSiteKey() {
        return boxConfirmNextSiteKey;
    }

    public void setBoxConfirmNextSiteKey(String boxConfirmNextSiteKey) {
        this.boxConfirmNextSiteKey = boxConfirmNextSiteKey;
    }
}
