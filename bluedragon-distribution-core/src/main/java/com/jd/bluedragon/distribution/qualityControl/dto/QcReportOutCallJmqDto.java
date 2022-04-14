package com.jd.bluedragon.distribution.qualityControl.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 质控上报异常提交jmq报文
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-02-16 16:19:45 周三
 */
public class QcReportOutCallJmqDto implements Serializable {

    private static final long serialVersionUID = -6296946929237365658L;

    //上报异常记录主键
    private Long id;

    //运单号
    private String abnormalDocumentNum;

    //包裹号
    private String packageNumber;

    //异常一级ID
    private Long abnormalFirstId;

    //异常一级名称
    private String abnormalFirstName;

    //异常二级ID
    private Long abnormalSecondId;

    //异常二级名称
    private String abnormalSecondName;

    //异常三级ID
    private Long abnormalThirdId;

    //异常三级名称
    private String abnormalThirdName;


    /*
    上报区域编号
    6：华北
    3：华东
    10：华南
    4：西南
    600：华中
    611：东北
    645：西北
     */
    private String createRegion;

    //上报部门编号
    private String createDept;

    //上报部门名称
    private String createDeptName;

    //上报时间
    private Long createTime;

    //上报人erp
    private String createUser;


    //上报详情描述
    private String remark;

    //上报附件信息 详情看AttachInfoDto 说明
    private List<AttachInfoDto> attachInfos;

    /**
     * 外呼状态
     *   0 ：未外呼 ，
     * 1：首次外呼，
     * 2：再次外呼，
     * 3：取消外呼，
     * 4：已达预期
     */
    private String outCallStatus;

    //完结时间
    private Long endTime;

    /**
     * 数据状态
     * 1：处理中
     * 5：已完结
     */
    private String endStatus;

    /**
     * 外呼结果
     * 0：未外呼
     * 70：外呼
     * 71：取消外呼
     * 72：驳回
     * 73：联系不上
     *  74：已回复
     * 75：已达预期
     * 76：未达预期
     * 77：未达预期-已回复
     * 78：未达预期-联系不上
     * -2：数据异常
     * -1 ：无对应概要
     *  1：普通同步留言日志
     * 2：事件升级并推送留言日志
     *  3：事件解决并推送留言日志
     *  24 ：已撤销
     */
    private String outCallResult;

    //客服返回结果
    private String customerServiceRemark;

    //上报系统来源
    private String reportSystem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbnormalDocumentNum() {
        return abnormalDocumentNum;
    }

    public void setAbnormalDocumentNum(String abnormalDocumentNum) {
        this.abnormalDocumentNum = abnormalDocumentNum;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public Long getAbnormalFirstId() {
        return abnormalFirstId;
    }

    public void setAbnormalFirstId(Long abnormalFirstId) {
        this.abnormalFirstId = abnormalFirstId;
    }

    public String getAbnormalFirstName() {
        return abnormalFirstName;
    }

    public void setAbnormalFirstName(String abnormalFirstName) {
        this.abnormalFirstName = abnormalFirstName;
    }

    public Long getAbnormalSecondId() {
        return abnormalSecondId;
    }

    public void setAbnormalSecondId(Long abnormalSecondId) {
        this.abnormalSecondId = abnormalSecondId;
    }

    public String getAbnormalSecondName() {
        return abnormalSecondName;
    }

    public void setAbnormalSecondName(String abnormalSecondName) {
        this.abnormalSecondName = abnormalSecondName;
    }

    public Long getAbnormalThirdId() {
        return abnormalThirdId;
    }

    public void setAbnormalThirdId(Long abnormalThirdId) {
        this.abnormalThirdId = abnormalThirdId;
    }

    public String getAbnormalThirdName() {
        return abnormalThirdName;
    }

    public void setAbnormalThirdName(String abnormalThirdName) {
        this.abnormalThirdName = abnormalThirdName;
    }

    public String getCreateRegion() {
        return createRegion;
    }

    public void setCreateRegion(String createRegion) {
        this.createRegion = createRegion;
    }

    public String getCreateDept() {
        return createDept;
    }

    public void setCreateDept(String createDept) {
        this.createDept = createDept;
    }

    public String getCreateDeptName() {
        return createDeptName;
    }

    public void setCreateDeptName(String createDeptName) {
        this.createDeptName = createDeptName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<AttachInfoDto> getAttachInfos() {
        return attachInfos;
    }

    public void setAttachInfos(List<AttachInfoDto> attachInfos) {
        this.attachInfos = attachInfos;
    }

    public String getOutCallStatus() {
        return outCallStatus;
    }

    public void setOutCallStatus(String outCallStatus) {
        this.outCallStatus = outCallStatus;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(String endStatus) {
        this.endStatus = endStatus;
    }

    public String getOutCallResult() {
        return outCallResult;
    }

    public void setOutCallResult(String outCallResult) {
        this.outCallResult = outCallResult;
    }

    public String getCustomerServiceRemark() {
        return customerServiceRemark;
    }

    public void setCustomerServiceRemark(String customerServiceRemark) {
        this.customerServiceRemark = customerServiceRemark;
    }

    public String getReportSystem() {
        return reportSystem;
    }

    public void setReportSystem(String reportSystem) {
        this.reportSystem = reportSystem;
    }

    @Override
    public String toString() {
        return "QcReportOutCallJmqDto{" +
                "id=" + id +
                ", abnormalDocumentNum='" + abnormalDocumentNum + '\'' +
                ", packageNumber='" + packageNumber + '\'' +
                ", abnormalFirstId=" + abnormalFirstId +
                ", abnormalFirstName='" + abnormalFirstName + '\'' +
                ", abnormalSecondId=" + abnormalSecondId +
                ", abnormalSecondName='" + abnormalSecondName + '\'' +
                ", abnormalThirdId=" + abnormalThirdId +
                ", abnormalThirdName='" + abnormalThirdName + '\'' +
                ", createRegion='" + createRegion + '\'' +
                ", createDept='" + createDept + '\'' +
                ", createDeptName='" + createDeptName + '\'' +
                ", createTime=" + createTime +
                ", createUser='" + createUser + '\'' +
                ", remark='" + remark + '\'' +
                ", attachInfos=" + attachInfos +
                ", outCallStatus='" + outCallStatus + '\'' +
                ", endTime=" + endTime +
                ", endStatus='" + endStatus + '\'' +
                ", outCallResult='" + outCallResult + '\'' +
                ", customerServiceRemark='" + customerServiceRemark + '\'' +
                ", reportSystem='" + reportSystem + '\'' +
                '}';
    }
}
