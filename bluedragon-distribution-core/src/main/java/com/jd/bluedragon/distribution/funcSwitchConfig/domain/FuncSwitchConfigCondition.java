package com.jd.bluedragon.distribution.funcSwitchConfig.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 站点开关配置请求
 *
 * @author: hujiping
 * @date: 2020/9/16 18:25
 */
public class FuncSwitchConfigCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 功能编码
     * */
    private Integer menuCode;
    /**
     * 功能名称
     * */
    private String menuName;
    /**
     * 维度编码
     * */
    private Integer dimensionCode;
    /**
     * 维度名称
     * */
    private String dimensionName;
    /**
     * 区域ID
     * */
    private Integer orgId;
    /**
     * 区域名称
     * */
    private String orgName;
    /**
     * 站点编码
     * */
    private Integer siteCode;
    /**
     * 站点名称
     * */
    private String siteName;
    /**
     * 操作人ERP
     * */
    private String operateErp;
    /**
     * 是否有效
     * */
    private Integer yn;

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

    public Integer getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(Integer dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
