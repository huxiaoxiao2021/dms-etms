package com.jd.bluedragon.common.dto.box.request;

import java.io.Serializable;

/**
 * 封箱请求body
 *
 * @author jiaowenqiang
 * @date 2019/7/26
 */
public class SealBoxTaskBody implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 封签号
     */
    private String sealCode;

    /**
     * 业务类型
     */

    private Integer businessType;

    /**
     * 操作人编号
     */
    private Integer userCode;

    /**
     * 操作人姓名
     */
    private String userName;

    /**
     * 分拣中心id
     */
    private Integer siteCode;

    /**
     * 分拣中心name
     */
    private String siteName;

    /**
     * 操作时间
     */
    private String operateTime;

    @Override
    public String toString() {
        return "SealBoxTaskBody{" +
                "boxCode='" + boxCode + '\'' +
                ", sealCode='" + sealCode + '\'' +
                ", businessType=" + businessType +
                ", userCode=" + userCode +
                ", userName='" + userName + '\'' +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", operateTime='" + operateTime + '\'' +
                '}';
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
