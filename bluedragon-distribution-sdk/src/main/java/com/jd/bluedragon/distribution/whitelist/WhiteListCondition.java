package com.jd.bluedragon.distribution.whitelist;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * @author lijie
 * @date 2020/3/10 16:20
 */
public class WhiteListCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 功能菜单
     * */
    private String menu;

    /**
     * 维度
     * */
    private String dimension;

    /**
     * 快运中心名称
     * */
    private String siteName;

    /**
     * 快运中心ID
     * */
    private String siteCode;

    /**
     * 白名单erp
     * */
    private String erp;

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }
}
