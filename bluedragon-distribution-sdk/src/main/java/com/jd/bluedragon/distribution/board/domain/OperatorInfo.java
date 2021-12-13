package com.jd.bluedragon.distribution.board.domain;

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
     * 场地类型
     */
    private Integer siteType;

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

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
        
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
        
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
        
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
        
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

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
        
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
        
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }
}
