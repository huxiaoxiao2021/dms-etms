package com.jd.bluedragon.common.dto.easyFreeze;



import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/9 14:55
 * @Description: 易冻品站点配置
 */
public class EasyFreezeSiteDto implements Serializable {
    private static final long serialVersionUID = 7371107674779976843L;

    private Long id;
    //站点编码
    private Integer siteCode;
    //站点名称
    private String siteName;
    //站点类型 分拣 or 转运
    private String siteType;

    //场地所在城市
    private String cityName;

    /**
     * 当前所在区域名称
     */
    private String orgName;
    //易冻提示开始时间
    private Date remindStartTime;
    //易冻提示结束时间
    private Date remindEndTime;
    //
    private Integer useState;

    /**
     * 创建人ERP
     */
    private String createUser;

    /**
     * 修改人ERP
     */
    private String updateUser;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Date getRemindStartTime() {
        return remindStartTime;
    }

    public void setRemindStartTime(Date remindStartTime) {
        this.remindStartTime = remindStartTime;
    }

    public Date getRemindEndTime() {
        return remindEndTime;
    }

    public void setRemindEndTime(Date remindEndTime) {
        this.remindEndTime = remindEndTime;
    }

    public Integer getUseState() {
        return useState;
    }

    public void setUseState(Integer useState) {
        this.useState = useState;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
