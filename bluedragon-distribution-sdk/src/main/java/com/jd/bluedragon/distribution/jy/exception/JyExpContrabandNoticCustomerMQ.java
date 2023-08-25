package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/31 16:52
 * @Description: 异常-破损通知客服MQ
 */
public class JyExpContrabandNoticCustomerMQ implements Serializable {
    private static final long serialVersionUID = 2L;



    /**业务类型标识 ,通用基础字段，必填  传408*/
    private int businessId;

    /**异常唯一标识,通用基础字段，必填 传系统业务唯一标志，回传时使用*/
    private String exptId;

    /**处理方式,通用基础字段，必填  传1 外呼*/
    private int dealType;

    /**线索值,通用基础字段，必填  传运单号 */
    private String codeInfo;

    /**线索类型,通用基础字段，必填  传50 表示运单*/
    private int codeType;


    /**异常发生时间,通用基础字段，必填  传上报时间*/
    private String exptCreateTime;


    /**异常一级编码,通用基础字段   传异常管控平台一级异常编号，怎么获取需要和鲁勇对接*/
    private String exptOneLevel;

    /**异常一级名称,通用基础字段  传异常管控平台一级异常名称，怎么获取需要和鲁勇对接*/
    private String exptOneLevelName;

    /**异常二级编码,通用基础字段 传异常管控平台二级异常编号，怎么获取需要和鲁勇对接*/
    private String exptTwoLevel;

    /**异常二级名称 ,通用基础字段 传异常管控平台二级异常名称，怎么获取需要和鲁勇对接*/
    private String exptTwoLevelName;

    /**上报部门编码,通用基础字段，必填  传上报部门code*/
    private String deptCode;

    /**上报部门名称,通用基础字段，必填 传上报部门名称*/
    private String deptName;

    /**异常发起人ERP,通用基础字段，必填 传erp或者汉字都可以*/
    private String exptCreator;

    /**
     * 省区编码
     */
    private String provinceAgencyCode;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    /**
     * 运单类型
     */
    private Integer waybillType;

    /**
     * 运单号
     */
    private String waybillCode;

    /**异常提报区域,通用基础字段，必填  传提报区域*/
    private String startOrgCode;

    /**异常提报区域名称,通用基础字段，必填  传提报区域名称*/
    private String startOrgName;



    /**包装个性化业务字段1,通用基础字段  ，传json数据，属于扩展字段，客服需要特殊数据可以通过该字段传*/
    private String personalized1;

    /**包装个性化业务字段2,通用基础字段 ，传json数据，属于扩展字段，客服需要特殊数据可以通过该字段传*/
    private String personalized2;

    /**包装个性化业务字段3,通用基础字段 ，传json数据，属于扩展字段，客服需要特殊数据可以通过该字段传*/
    private String personalized3;

    /**描述,通用基础字段   传外呼诉求描述*/
    private String remark;



    /**附件地址,通用基础字段  传附件地址，用逗号分隔*/
    private String attachmentAddr;


    public Integer getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }

    public int getDealType() {
        return dealType;
    }

    public void setDealType(int dealType) {
        this.dealType = dealType;
    }

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public int getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getExptCreateTime() {
        return exptCreateTime;
    }

    public void setExptCreateTime(String exptCreateTime) {
        this.exptCreateTime = exptCreateTime;
    }

    public String getExptCreator() {
        return exptCreator;
    }

    public void setExptCreator(String exptCreator) {
        this.exptCreator = exptCreator;
    }

    public String getExptId() {
        return exptId;
    }

    public void setExptId(String exptId) {
        this.exptId = exptId;
    }

    public String getExptOneLevel() {
        return exptOneLevel;
    }

    public void setExptOneLevel(String exptOneLevel) {
        this.exptOneLevel = exptOneLevel;
    }

    public String getExptOneLevelName() {
        return exptOneLevelName;
    }

    public void setExptOneLevelName(String exptOneLevelName) {
        this.exptOneLevelName = exptOneLevelName;
    }

    public String getExptTwoLevel() {
        return exptTwoLevel;
    }

    public void setExptTwoLevel(String exptTwoLevel) {
        this.exptTwoLevel = exptTwoLevel;
    }

    public String getExptTwoLevelName() {
        return exptTwoLevelName;
    }

    public void setExptTwoLevelName(String exptTwoLevelName) {
        this.exptTwoLevelName = exptTwoLevelName;
    }



    public String getPersonalized1() {
        return personalized1;
    }

    public void setPersonalized1(String personalized1) {
        this.personalized1 = personalized1;
    }

    public String getPersonalized2() {
        return personalized2;
    }

    public void setPersonalized2(String personalized2) {
        this.personalized2 = personalized2;
    }

    public String getPersonalized3() {
        return personalized3;
    }

    public void setPersonalized3(String personalized3) {
        this.personalized3 = personalized3;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartOrgCode() {
        return startOrgCode;
    }

    public void setStartOrgCode(String startOrgCode) {
        this.startOrgCode = startOrgCode;
    }

    public String getStartOrgName() {
        return startOrgName;
    }

    public void setStartOrgName(String startOrgName) {
        this.startOrgName = startOrgName;
    }

    public String getAttachmentAddr() {
        return attachmentAddr;
    }

    public void setAttachmentAddr(String attachmentAddr) {
        this.attachmentAddr = attachmentAddr;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }
}
