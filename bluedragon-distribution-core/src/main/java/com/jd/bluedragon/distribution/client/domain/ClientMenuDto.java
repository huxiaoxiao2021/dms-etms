package com.jd.bluedragon.distribution.client.domain;

import java.io.Serializable;

/**
 * 客户端菜单
 *
 * @author hujiping
 * @date 2021/10/9 6:02 下午
 */
public class ClientMenuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单编码
     */
    private Integer menuCode;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 是否可打印
     */
    private Boolean isCanPrint = true;
    /**
     * 是否可称重
     */
    private Boolean isCanWeight = true;

    public Integer getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(Integer menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
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
