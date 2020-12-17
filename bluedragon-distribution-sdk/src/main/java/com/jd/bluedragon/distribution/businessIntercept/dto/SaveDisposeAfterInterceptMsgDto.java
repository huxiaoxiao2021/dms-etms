package com.jd.bluedragon.distribution.businessIntercept.dto;

import java.io.Serializable;

/**
 * 拦截后处理工作消息报表
 *
 * @author fanggang7
 * @time 2020-12-11 17:26:14 周五
 */
public class SaveDisposeAfterInterceptMsgDto implements Serializable {

    private static final long serialVersionUID = 5587512028695038563L;

    /**
     * 单据号
     */
    private String barCode;

    /**
     * 应对操作
     */
    private Integer disposeNode;

    /**
     * 场地ID
     */
    private Integer siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 操作用户ERP
     */
    private String operateUserErp;

    /**
     * 操作用户编码
     */
    private Integer operateUserCode;

    /**
     * 操作用户名称
     */
    private String operateUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getDisposeNode() {
        return disposeNode;
    }

    public void setDisposeNode(Integer disposeNode) {
        this.disposeNode = disposeNode;
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

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "SaveDisposeAfterInterceptMsgDto{" +
                "barCode='" + barCode + '\'' +
                ", disposeNode=" + disposeNode +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", operateUserErp='" + operateUserErp + '\'' +
                ", operateUserCode=" + operateUserCode +
                ", operateUserName='" + operateUserName + '\'' +
                ", operateTime=" + operateTime +
                '}';
    }
}
