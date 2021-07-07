package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * PDA系统菜单请求
 *
 * @author hujiping
 * @date 2021/7/1 8:06 下午
 */
public class PdaSystemMenuRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单编码
     */
    private String menuCode;
    /**
     * 站点ID
     */
    private Integer siteCode;
    /**
     * 用户ERP
     */
    private String userCode;

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
