package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/11 14:54
 * @Description
 */
public class PickingGoodScanTaskBodyDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // 箱号
    private String boxCode;
    // 业务类型(10:自营，20:逆向，30:三方)
    private int businessType;

    //业务数据类型(分拣，发货，收货，验货等等。。)
//1810---空铁提货 离线提货
//1811---空铁提货 离线提货并发货
    private int taskType;

    // 用户erp编号
    private String userErp;
    // 用户编号
    private int userCode;
    // 用户名称
    private String userName;
    // 站点编号
    private int siteCode;
    private String siteName;
    // 操作时间
    private String operateTime;

    //批次号
    private String batchCode;
    //发货批次流向
    private int receiveSiteCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public int getUserCode() {
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

    public int getSiteCode() {
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

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public int getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(int receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
}
