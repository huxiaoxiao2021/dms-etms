package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作人信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-22 10:25:06 周日
 */
public class OperatorInfo implements Serializable {

    private static final long serialVersionUID = -8484747161337757178L;
    /**
     * 操作人编号
     */
    private Integer userCode;

    /**
     * 操作人姓名
     */
    private String userName;

    /**
     * 操作人ERP
     */
    private String userErp;

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

    /**
     *操作时间
     */
    private Date operateTime;

    /**
     * 操作来源 1: 分拣pda 2:分拣机
     * @return
     */
    private Integer bizSource;

    /**
     * 版本：做数据隔离
     */
    private Integer version;

    public Integer getUserCode() {
        return userCode;
    }

    public OperatorInfo setUserCode(int userCode) {
        this.userCode = userCode;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public OperatorInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserErp() {
        return userErp;
    }

    public OperatorInfo setUserErp(String userErp) {
        this.userErp = userErp;
        return this;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public OperatorInfo setSiteCode(int siteCode) {
        this.siteCode = siteCode;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public OperatorInfo setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public OperatorInfo setOrgId(Integer orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getOrgName() {
        return orgName;
    }

    public OperatorInfo setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public OperatorInfo setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
        return this;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public OperatorInfo setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
