package com.jd.bluedragon.distribution.client.domain;

import com.jd.bluedragon.distribution.client.enums.ClientMenuEnum;


/**
 * 客户端菜单功能配置
 *
 * @author hujiping
 * @date 2021/10/13 6:17 下午
 */
public class ClientMenuFuncConfig {

    /**
     * 菜单编码
     * @see ClientMenuEnum
     */
    private String menuCode;
    /**
     * 站点类型
     */
    private Integer siteType;
    /**
     * 站点子类型
     */
    private Integer subType;
    /**
     * 是否可打印
     */
    private Boolean isCanPrint = true;
    /**
     * 是否可称重量方
     */
    private Boolean isCanWeight = true;

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
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

    public Boolean getIsCanPrint() {
        return isCanPrint;
    }

    public void setIsCanPrint(Boolean canPrint) {
        isCanPrint = canPrint;
    }

    public Boolean getIsCanWeight() {
        return isCanWeight;
    }

    public void setIsCanWeight(Boolean canWeight) {
        isCanWeight = canWeight;
    }
}
