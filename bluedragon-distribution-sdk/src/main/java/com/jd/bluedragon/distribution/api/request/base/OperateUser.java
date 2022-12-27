package com.jd.bluedragon.distribution.api.request.base;

import java.io.Serializable;

/**
 * 基础用户信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-15 20:49:26 周一
 */
public class OperateUser implements Serializable {

    private static final long serialVersionUID = 7274400728122958189L;

    private String userCode;

    private Long userId;

    private String userName;

    /**
     *操作单位编号
     */
    private Integer siteCode;

    /**
     *操作单位名称
     */
    private String siteName;

    /**
     * 当前所在区域编号
     */
    private Integer orgId;

    /**
     * 当前所在区域名称
     */
    private String orgName;

    /**
     *分拣中心编码
     */
    private String dmsCode;

    public OperateUser() {
    }

    public String getUserCode() {
        return userCode;
    }

    public OperateUser setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public OperateUser setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public OperateUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public OperateUser setSiteCode(int siteCode) {
        this.siteCode = siteCode;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public OperateUser setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public OperateUser setOrgId(Integer orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public OperateUser setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public OperateUser setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
        return this;
    }
}
