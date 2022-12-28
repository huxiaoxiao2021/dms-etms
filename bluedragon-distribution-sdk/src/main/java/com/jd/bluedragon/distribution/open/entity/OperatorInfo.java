package com.jd.bluedragon.distribution.open.entity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: OperatorInfo
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 10:04
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class OperatorInfo {

    /**
     * 操作人ID
     */
    private Integer operateUserId;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 操作场地ID
     */
    private Integer operateSiteId;

    /**
     * 操作场地编码
     */
    private String operateSiteCode;

    /**
     * 操作场地名称
     */
    private String operateSiteName;

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
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

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }
}
