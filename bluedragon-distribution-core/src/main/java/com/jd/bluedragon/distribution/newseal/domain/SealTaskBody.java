package com.jd.bluedragon.distribution.newseal.domain;

import java.io.Serializable;

/**
 * 封车任务body
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年03月18日 20时:56分
 */
public class SealTaskBody implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 任务类型：1880 */
    private Integer taskType;

    /** 多个批次号，以英文半角逗号分隔 */
    private String batchCode;

    /** 格式yyyy-MM-dd HH:mm:ss.SSS */
    private String operateTime;

    /** 多个封签号，以英文半角逗号分隔 */
    private String shieldsCarCode;

    /** 封车场地ID */
    private Integer siteCode;

    /** 目的场地ID */
    private Integer receiveSiteCode;

    /** 封车场地Name */
    private String siteName;

    /** 封车场地Name */
    private Integer userCode;

    /** 封车场地Name */
    private String userName;

    /** 运力编码 */
    private String sealBoxCode;

    /** 车牌号 */
    private String carCode;

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getShieldsCarCode() {
        return shieldsCarCode;
    }

    public void setShieldsCarCode(String shieldsCarCode) {
        this.shieldsCarCode = shieldsCarCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
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

    public String getSealBoxCode() {
        return sealBoxCode;
    }

    public void setSealBoxCode(String sealBoxCode) {
        this.sealBoxCode = sealBoxCode;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }
}
