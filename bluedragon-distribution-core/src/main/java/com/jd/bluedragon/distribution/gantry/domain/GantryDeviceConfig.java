package com.jd.bluedragon.distribution.gantry.domain;

import com.jd.bluedragon.utils.BooleanHelper;

import java.util.Date;

/**
 * Created by yanghongqiang on 2016/3/11.
 * 龙门架编号，操作时间，返回操作类型，操作人ID、姓名、操作站点ID，名称、批次号（可选）等其它信息
 * "siteCode":2015,"siteName":"北京双树分拣中心","staffName":"杨宏强","orgId":6,"dmsCode":"010F005"
 */
public class GantryDeviceConfig {

    /*
    龙门架编号
     */
    private String gantryNum;

    /**
     * 操作人id
     */
    private int operteUserId;

    /*
    *操作人erp帐户
     */
    private String operteUser;

    /*
    *操作人姓名
    */
    private String operteUserName;


    /*
    *结束批次人erp帐户
     */
    private String updateUser;

    /*
    *结束批次人姓名
    */
    private String updateUserName;

    /*
    操作人所属站点
     */
    private Integer siteCode;

    /*
    操作人所属分拣中心
     */
    private String siteName;

    /*
    操作类型,按位求于,1验货，2发货，4量方。5验货+量方。6发货+量方
     */
    private Integer operateType;

    /*
    用文字表达的操作类型如：验货+量方,对应二进制101，10进至5.
     */
    private String operateTypeRmark;

    /*
    批次号
     */
    private String sendCode;

    /*
    批次号开始时间
     */
    private Date startTime;

    /*
    批次号结束时间
     */
    private Date endTime;


    public String getGantryNum() {
        return gantryNum;
    }

    public void setGantryNum(String gantryNum) {
        this.gantryNum = gantryNum;
    }

    public int getOperteUserId() {
        return operteUserId;
    }

    public void setOperteUserId(int operteUserId) {
        this.operteUserId = operteUserId;
    }

    public String getOperteUser() {
        return operteUser;
    }

    public void setOperteUser(String operteUser) {
        this.operteUser = operteUser;
    }

    public String getOperteUserName() {
        return operteUserName;
    }

    public void setOperteUserName(String operteUserName) {
        this.operteUserName = operteUserName;
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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperateTypeRmark() {
        return operateTypeRmark;
    }

    public void setOperateTypeRmark(String operateTypeRmark) {
        this.operateTypeRmark = operateTypeRmark;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
