package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api.request
 * @ClassName: TransAbnormalDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/25 10:56
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class TransAbnormalDto implements Serializable {

    /**
     * 任务号
     */
    private String referBillCode;

    /**
     * 3-任务号TJ
     */
    private Byte referBillType;

    /**
     * 异常一级分类
     */
    private String abnormalSort1Code;
    private String abnormalSort1Name;

    /**
     * 异常二级分类
     */
    private String abnormalSort2Code;
    private String abnormalSort2Name;

    /**
     * 事件类型
     */
    private String abnormalTypeCode;
    private String abnormalTypeName;

    /**
     * 异常封签列表
     */
    private String abnormalWaybillCode;

    /**
     * 图片URL列表
     */
    private List<String> photoUrlList;

    /**
     * 用户名
     */
    private String userName;

    /**
     * erp
     */
    private String userCode;

    /**
     * 异常描述
     */
    private String abnormalDesc;

    /**
     * 提报站点
     */
    private Integer siteCode;

    /**
     * 提报站点名称
     */
    private String siteName;

    public String getReferBillCode() {
        return referBillCode;
    }

    public void setReferBillCode(String referBillCode) {
        this.referBillCode = referBillCode;
    }

    public Byte getReferBillType() {
        return referBillType;
    }

    public void setReferBillType(Byte referBillType) {
        this.referBillType = referBillType;
    }

    public String getAbnormalSort1Code() {
        return abnormalSort1Code;
    }

    public void setAbnormalSort1Code(String abnormalSort1Code) {
        this.abnormalSort1Code = abnormalSort1Code;
    }

    public String getAbnormalSort1Name() {
        return abnormalSort1Name;
    }

    public void setAbnormalSort1Name(String abnormalSort1Name) {
        this.abnormalSort1Name = abnormalSort1Name;
    }

    public String getAbnormalSort2Code() {
        return abnormalSort2Code;
    }

    public void setAbnormalSort2Code(String abnormalSort2Code) {
        this.abnormalSort2Code = abnormalSort2Code;
    }

    public String getAbnormalSort2Name() {
        return abnormalSort2Name;
    }

    public void setAbnormalSort2Name(String abnormalSort2Name) {
        this.abnormalSort2Name = abnormalSort2Name;
    }

    public String getAbnormalTypeCode() {
        return abnormalTypeCode;
    }

    public void setAbnormalTypeCode(String abnormalTypeCode) {
        this.abnormalTypeCode = abnormalTypeCode;
    }

    public String getAbnormalTypeName() {
        return abnormalTypeName;
    }

    public void setAbnormalTypeName(String abnormalTypeName) {
        this.abnormalTypeName = abnormalTypeName;
    }

    public String getAbnormalWaybillCode() {
        return abnormalWaybillCode;
    }

    public void setAbnormalWaybillCode(String abnormalWaybillCode) {
        this.abnormalWaybillCode = abnormalWaybillCode;
    }

    public List<String> getPhotoUrlList() {
        return photoUrlList;
    }

    public void setPhotoUrlList(List<String> photoUrlList) {
        this.photoUrlList = photoUrlList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAbnormalDesc() {
        return abnormalDesc;
    }

    public void setAbnormalDesc(String abnormalDesc) {
        this.abnormalDesc = abnormalDesc;
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
}
