package com.jd.bluedragon.distribution.client.domain;

import com.jd.bluedragon.distribution.client.enums.ClientMenuEnum;

import java.io.Serializable;

/**
 * 客户端权限请求体
 *
 * @author hujiping
 * @date 2021/10/9 5:58 下午
 */
public class ClientAuthRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 站点ID
     */
    private Integer siteCode;

    /**
     * 站点类型
     */
    private Integer siteType;

    /**
     * 站点子类型
     */
    private Integer subType;

    /**
     * 登录人ERP
     */
    private String loginErp;

    /**
     * 菜单编码
     * @see ClientMenuEnum
     */
    private Integer menuCode;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public String getLoginErp() {
        return loginErp;
    }

    public void setLoginErp(String loginErp) {
        this.loginErp = loginErp;
    }

    public Integer getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(Integer menuCode) {
        this.menuCode = menuCode;
    }
}
