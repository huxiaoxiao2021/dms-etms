package com.jd.bluedragon.distribution.external.domain;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/24 15:56
 */
public class DmsFuncSwitchDto {

    /**
     * 功能编码
     */
    private Integer menuCode;

    /**
     * 站点编码
     */
    private Integer  siteCode;

    /**
     * 是否拦截标识  1:不拦截  0:拦截
     */
    private Integer  ynFilterCode;

    public DmsFuncSwitchDto() {
    }

    public DmsFuncSwitchDto(Integer menuCode, Integer siteCode, Integer ynFilterCode) {
        this.menuCode = menuCode;
        this.siteCode = siteCode;
        this.ynFilterCode = ynFilterCode;
    }

    public Integer getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(Integer menuCode) {
        this.menuCode = menuCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getYnFilterCode() {
        return ynFilterCode;
    }

    public void setYnFilterCode(Integer ynFilterCode) {
        this.ynFilterCode = ynFilterCode;
    }
}
    
