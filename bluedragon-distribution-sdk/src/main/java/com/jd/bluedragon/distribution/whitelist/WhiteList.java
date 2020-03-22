package com.jd.bluedragon.distribution.whitelist;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lijie
 * @date 2020/3/10 16:01
 */
public class WhiteList implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     * */
    private Long id;

    /**
     * 功能ID
     * */
    private Integer menuId;

    /**
     * 功能名称
     * */
    private String menuName;

    /**
     * 维度ID
     * */
    private Integer dimensionId;

    /**
     * 维度名称
     * */
    private String dimensionName;

    /**
     * 分拣中心名称
     * */
    private String siteName;

    /**
     * 分拣中心ID
     * */
    private Integer siteCode;

    /**
     * 白名单erp
     * */
    private String erp;

    /**
     * 创建人姓名
     * */
    private String createUser;

    /**
     * 创建时间
     * */
    private Date createTime;

    /**
     * 更新人姓名
     * */
    private String updateUser;

    /**
     * 更新时间
     * */
    private Date updateTime;

    /**
     * 是否有效
     */
    private Integer isDelete;
    /**
     * 数据库时间
     */
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(Integer dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
