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
public class QcReportJmqDto implements Serializable {

    private static final long serialVersionUID = -8763126814581827741L;

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

    //特殊场景
    private String specialScene;

    /**
     * 上报区域编号
     * 6：华北
     * 3：华东
     * 10：华南
     * 4：西南
     * 600：华中
     * 611：东北
     * 645：西北
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

    /**
     * 处理区域
     * 6：华北
     * 3：华东
     * 10：华南
     * 4：西南
     * 600：华中
     * 611：东北
     * 645：西北
     */
    private String dealRegion;

    //处理部门
    private String dealDept;

    //处理部门名称
    private String dealDeptName;

    //上报附件信息 详情看AttachInfoDto 说明
    private List<AttachInfoDto> attachInfos;

    /**
     * 责任区域
     * 6：华北
     * 3：华东
     * 10：华南
     * 4：西南
     * 600：华中
     * 611：东北
     * 645：西北
     */
    private String dutyRegion;

    //责任部门
    private String dutyDept;

    //责任部门名称
    private String dutyDeptName;

    /**
     * 异常状态
     * 1：初始
     * 2：转发
     * 4：认责
     * 5：判责定责
     * 6：判责定责-无责闭环
     * 8：丢失找到
     * 12：无责闭环
     * 22：升级判责
     * 24：已撤销
     * 30：责任重算
     * 31：补充附件
     */
    private String abnormalStatus;

    /**
     * 单据状态
     * 1：处理中
     * 5：已完结
     */
    private String endStatus;

    /**
     *完成原因
     * 0：无
     * 2：已操作
     * 3：妥投
     * 4：退库完结
     * 5：经济网部门确认
     * 6：包裹号无效
     * 7：包裹延迟闭环
     * 8：操作报废
     * 9：疫情延误
     * 10:其他
     * 11：丢失找到-双(多)面单
     * 12：丢失找到-金盾手动扯淡
     * 13：丢失找到-识别仓储入仓
     * 15：丢失找到-理赔拦截
     * 16：丢失找到-认责
     * 17：同部门五次抓取
     * 18：时效内无异常
     * 19：非运营责任
     * 20：理赔取消-无责闭环
     * 21：报废
     */
    private String endReason;


    //上报系统，接入时分配
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

    public String getSpecialScene() {
        return specialScene;
    }

    public void setSpecialScene(String specialScene) {
        this.specialScene = specialScene;
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

    public String getDealRegion() {
        return dealRegion;
    }

    public void setDealRegion(String dealRegion) {
        this.dealRegion = dealRegion;
    }

    public String getDealDept() {
        return dealDept;
    }

    public void setDealDept(String dealDept) {
        this.dealDept = dealDept;
    }

    public String getDealDeptName() {
        return dealDeptName;
    }

    public void setDealDeptName(String dealDeptName) {
        this.dealDeptName = dealDeptName;
    }

    public List<AttachInfoDto> getAttachInfos() {
        return attachInfos;
    }

    public void setAttachInfos(List<AttachInfoDto> attachInfos) {
        this.attachInfos = attachInfos;
    }

    public String getDutyRegion() {
        return dutyRegion;
    }

    public void setDutyRegion(String dutyRegion) {
        this.dutyRegion = dutyRegion;
    }

    public String getDutyDept() {
        return dutyDept;
    }

    public void setDutyDept(String dutyDept) {
        this.dutyDept = dutyDept;
    }

    public String getDutyDeptName() {
        return dutyDeptName;
    }

    public void setDutyDeptName(String dutyDeptName) {
        this.dutyDeptName = dutyDeptName;
    }

    public String getAbnormalStatus() {
        return abnormalStatus;
    }

    public void setAbnormalStatus(String abnormalStatus) {
        this.abnormalStatus = abnormalStatus;
    }

    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(String endStatus) {
        this.endStatus = endStatus;
    }

    public String getEndReason() {
        return endReason;
    }

    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }

    public String getReportSystem() {
        return reportSystem;
    }

    public void setReportSystem(String reportSystem) {
        this.reportSystem = reportSystem;
    }

    @Override
    public String toString() {
        return "QcReportJmqDto{" +
                "id=" + id +
                ", abnormalDocumentNum='" + abnormalDocumentNum + '\'' +
                ", packageNumber='" + packageNumber + '\'' +
                ", abnormalFirstId='" + abnormalFirstId + '\'' +
                ", abnormalFirstName='" + abnormalFirstName + '\'' +
                ", abnormalSecondId='" + abnormalSecondId + '\'' +
                ", abnormalSecondName='" + abnormalSecondName + '\'' +
                ", abnormalThirdId='" + abnormalThirdId + '\'' +
                ", abnormalThirdName='" + abnormalThirdName + '\'' +
                ", specialScene='" + specialScene + '\'' +
                ", createRegion='" + createRegion + '\'' +
                ", createDept='" + createDept + '\'' +
                ", createDeptName='" + createDeptName + '\'' +
                ", createTime=" + createTime +
                ", createUser='" + createUser + '\'' +
                ", remark='" + remark + '\'' +
                ", dealRegion='" + dealRegion + '\'' +
                ", dealDept='" + dealDept + '\'' +
                ", dealDeptName='" + dealDeptName + '\'' +
                ", attachInfos=" + attachInfos +
                ", dutyRegion='" + dutyRegion + '\'' +
                ", dutyDept='" + dutyDept + '\'' +
                ", dutyDeptName='" + dutyDeptName + '\'' +
                ", abnormalStatus='" + abnormalStatus + '\'' +
                ", endStatus='" + endStatus + '\'' +
                ", endReason='" + endReason + '\'' +
                ", reportSystem='" + reportSystem + '\'' +
                '}';
    }
}
