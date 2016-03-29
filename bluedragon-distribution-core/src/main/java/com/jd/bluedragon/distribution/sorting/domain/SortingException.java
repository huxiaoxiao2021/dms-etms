package com.jd.bluedragon.distribution.sorting.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * 分拣拦截异常记录
 * Created by wangtingwei on 2014/10/21.
 */
public class SortingException implements Serializable{

    private static final long serialVersionUID=1L;
    /**
     * 记录ID
     */
    private int id;

    /**
     * 始发站点ID
     */
    @SerializedName("siteCode")
    private Integer createSiteCode;

    /**
     * 接收站点ID
     */
    private Integer receiveSiteCode;

    /**
     * 业务类型【正向、逆向、三方】
     */
    private Integer businessType;

    /**
     * 箱号
     */
    private String  boxCode;

    /**
     * 包裹号
     */
    private String  packageCode;

    /**
     *拦截代号
     */
    private Integer exceptionCode;

    /**
     * 拦截信息
     */
    private String  exceptionMessage;

    /**
     *创建用户ID
     */
    @SerializedName("userCode")
    private Integer createUserCode;

    /**
     * 创建用户姓名
     */
    @SerializedName("userName")
    private String  createUserName;

    /**
     *更新用户ID
     */
    private Integer updateUserCode;

    /**
     * 更新用户姓名
     */
    private String  updateUserName;

    /**
     * 创建时间
     */
    @SerializedName("operateTime")
    private Date    createTime;

    /**
     * 更新时间
     */
    private Date    updateTime;

    /**
     * 标记
     */
    private int     yn;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(Integer exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getYn() {
        return yn;
    }

    public void setYn(int yn) {
        this.yn = yn;
    }

    @Override
    public String toString() {
        return "SortingException{" +
                "Id=" + id +
                ", createSiteCode=" + createSiteCode +
                ", receiveSiteCode=" + receiveSiteCode +
                ", businessType=" + businessType +
                ", boxCode='" + boxCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", exceptionCode=" + exceptionCode +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", createUserCode=" + createUserCode +
                ", createUserName='" + createUserName + '\'' +
                ", updateUserCode=" + updateUserCode +
                ", updateUserName='" + updateUserName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", yn=" + yn +
                '}';
    }

}
