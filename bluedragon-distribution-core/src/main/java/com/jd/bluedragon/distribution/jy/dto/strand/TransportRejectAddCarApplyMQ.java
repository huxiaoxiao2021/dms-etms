package com.jd.bluedragon.distribution.jy.dto.strand;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 加车申请运输驳回MQ
 * @see https://cf.jd.com/pages/viewpage.action?pageId=1251374768
 *
 * @author hujiping
 * @date 2023/4/3 1:50 PM
 */
public class TransportRejectAddCarApplyMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    // 运输计划号-消息唯一码
    private String transPlanCode;
    // 预计发车时间
    private Date planDepartTime;
    // 始发网点编码
    private String beginNodeCode;
    // 始发网点名称
    private String beginNodeName;
    // 目的网点编码
    private String endNodeCode;
    // 目的网点名称
    private String endNodeName;
    // 运输方式:1-整车,2-零担
    private Integer transWay;
    // 线路类型
    private Integer transType;
    // 业务来源:170-分拣提报
    private Integer businessSource;
    // 原因编号
    private Integer reasonCode;
    // 原因备注
    private String reasonRemark;
    // 原因名称
    private String reasonName;
    // 原因大类
    private String reasonType;
    // 原因大类名称
    private String reasonTypeName;
    // 操作时间
    private Date operateTime;
    // 创建时间
    private Date createTime;
    // 创建人ERP
    private String createUserCode;
    // 图片链接
    private List<String> photoUrlList;

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public Integer getBusinessSource() {
        return businessSource;
    }

    public void setBusinessSource(Integer businessSource) {
        this.businessSource = businessSource;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonRemark() {
        return reasonRemark;
    }

    public void setReasonRemark(String reasonRemark) {
        this.reasonRemark = reasonRemark;
    }

    public String getReasonName() {
        return reasonName;
    }

    public void setReasonName(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }

    public String getReasonTypeName() {
        return reasonTypeName;
    }

    public void setReasonTypeName(String reasonTypeName) {
        this.reasonTypeName = reasonTypeName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(String createUserCode) {
        this.createUserCode = createUserCode;
    }

    public List<String> getPhotoUrlList() {
        return photoUrlList;
    }

    public void setPhotoUrlList(List<String> photoUrlList) {
        this.photoUrlList = photoUrlList;
    }
}
