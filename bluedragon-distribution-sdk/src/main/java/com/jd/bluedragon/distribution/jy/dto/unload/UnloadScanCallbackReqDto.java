package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2024/1/18
 * @Description:
 */
public class UnloadScanCallbackReqDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 单号
     */
    private String barCode;
    /**
     * 扫描方式
     */
    private Integer scanType;
    /**
     * 任务主键
     */
    private String taskId;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 跳过拦截强制提交
     */
    private Boolean forceSubmit;
    /**
     * 任务组号
     */
    private String groupCode;
    /**
     *操作单位编号
     */
    private int siteCode;
    /**
     *操作单位名称
     */
    private String siteName;
    /**
     * 操作人编号
     */
    private int userCode;
    /**
     * 操作人姓名
     */
    private String userName;
    /**
     * erp
     */
    private String userErp;
    /**
     *操作时间
     */
    private Date operateTime;
    /**
     * 装卸商编码
     */
    private String merchantCode;
    /**
     * 装卸商名称
     */
    private Long merchantName;

    //==================================

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Boolean getForceSubmit() {
        return forceSubmit;
    }

    public void setForceSubmit(Boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Long getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(Long merchantName) {
        this.merchantName = merchantName;
    }
}
