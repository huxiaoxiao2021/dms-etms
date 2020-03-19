package com.jd.bluedragon.distribution.whitelist;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * @author lijie
 * @date 2020/3/10 16:20
 */
public class WhiteListCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 功能名称
     * */
    private String menuName;

    /**
     * 分拣中心名称
     * */
    private String siteName;

    /**
     * 白名单erp
     * */
    private String erp;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }
}
