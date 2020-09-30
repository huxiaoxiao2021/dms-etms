package com.jd.bluedragon.distribution.funcSwitchConfig;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * 站点开关配置DTO
 *
 * @author: hujiping
 * @date: 2020/9/16 18:26
 */
public class FuncSwitchConfigDto extends DbEntity {

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
     * 创建人ERP
     * */
    private String createErp;
    /**
     * 创建人名称
     * */
    private String createUser;
    /**
     * 创建时间
     * */
    private Date createTime;
    /**
     * 变更时间
     * */
    private Date updateTime;
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

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
