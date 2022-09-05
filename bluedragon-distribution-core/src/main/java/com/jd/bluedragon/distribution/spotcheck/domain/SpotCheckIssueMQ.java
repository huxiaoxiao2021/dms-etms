package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 抽检异常下发MQ实体
 *
 * @author hujiping
 * @date 2021/12/14 9:01 下午
 */
public class SpotCheckIssueMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流程发起系统
     * DMS-DWS："分拣-设备抽检"
     * DMS-MSI："分拣-人工抽检",
     * ZD-KD："终端-快递",
     * ZD-KY："终端-快运";
     */
    private String flowSystem;

    // 流程发起环节,1：拣运 2：终端
    private String initiationLink;

    // 数据来源系统  1:分拣设备抽检，2:分拣人工抽检；3：终端快递；4:终端快运；5:判责系统 6:称重再造系统
    private String sysSource;

    // 流程唯一标识
    private String flowId;

    // 数据类型；1：发起流程 2：更新状态
    private Integer operateType;

    // 运单号
    private String waybillCode;

    // 责任类型 1:仓 2:分拣 3:站点（终端） 4:商家
    private Integer dutyType;

    // 责任区域
    private String dutyRegion;

    // 责任区域编码
    private String dutyRegionCode;

    // 责任战区
    private String dutyProvinceCompanyName;

    // 责任战区编码
    private String dutyProvinceCompanyCode;

    // 责任片区编码
    private String dutyAreaCode;

    // 责任片区
    private String dutyAreaName;

    // 责任站点(或者分拣)
    private String dutyOrgCode;

    // 站点名称(或者分拣)
    private String dutyOrgName;

    // 责任人账号 自营员工为erp，三方员工为pin
    private String dutyStaffAccount;

    // 员工类型 1-自营员工 2-三方员工
    private Integer dutyStaffType;

    // 责任人姓名
    private String dutyStaffName;

    // 复核长
    private String reConfirmLong;

    // 复核宽
    private String reConfirmWidth;

    // 复核高
    private String reConfirmHigh;

    // 发起人
    private String startStaffAccount;

    // 员工类型 1-自营员工 2-三方员工
    private Integer startStaffType;

    // 发起人名字
    private String startStaffName;

    // 发起区域
    private String startRegion;

    // 发起区域编码
    private String startRegionCode;

    // 发起站点（或者分拣中心）编码
    private String orgCode;

    // 发起站点（或者分拣中心）名称
    private String orgName;

    // 被举报重量 发起流程有异议的重量，单位为kg
    private String confirmWeight;

    // 被举报体积 发起流程有异议的体积，单位为cm3
    private String confirmVolume;

    // 举报重量 发起流程时复核的重量，单位为kg
    private String reConfirmWeight;

    // 举报体积 发起流程有复核的体积，单位为cm3
    private String reConfirmVolume;

    // 重泡比
    private String convertCoefficient;

    // 重量差异
    private String diffWeight;

    // 差异标准
    private String standerDiff;

    // 超标类型 1:重量超标 2:体积超标 3:未超标
    private Integer exceedType;

    // 核对重量来源 核对重量来源（计费/复重/下单/无）1-计费重量 2-运单复重 3-下单重量
    private Integer confirmWeightSource;

    // 流程发起时间 格式 "yyyy-MM-dd HH:mm:ss";只在流程发起时初始化一次
    private Date startTime;

    // 1：待核实 2：认责 3：超时认责 4：系统认责 5：升级判责 6：判责有效 7：判责无效 8：处理完成
    private Integer status;

    // 状态时间，更新状态时须填写 "yyyy-MM-dd HH:mm:ss"
    private Date statusUpdateTime;

    // 附件类型 1:图片，2:视频
    private Integer appendix;

    // 附件URL
    private List<String> url;

    // 描述
    private String comment;

    // 补数字段
    private String errCode;

    public String getFlowSystem() {
        return flowSystem;
    }

    public void setFlowSystem(String flowSystem) {
        this.flowSystem = flowSystem;
    }

    public String getInitiationLink() {
        return initiationLink;
    }

    public void setInitiationLink(String initiationLink) {
        this.initiationLink = initiationLink;
    }

    public String getSysSource() {
        return sysSource;
    }

    public void setSysSource(String sysSource) {
        this.sysSource = sysSource;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getDutyType() {
        return dutyType;
    }

    public void setDutyType(Integer dutyType) {
        this.dutyType = dutyType;
    }

    public String getDutyRegion() {
        return dutyRegion;
    }

    public void setDutyRegion(String dutyRegion) {
        this.dutyRegion = dutyRegion;
    }

    public String getDutyRegionCode() {
        return dutyRegionCode;
    }

    public void setDutyRegionCode(String dutyRegionCode) {
        this.dutyRegionCode = dutyRegionCode;
    }

    public String getDutyProvinceCompanyName() {
        return dutyProvinceCompanyName;
    }

    public void setDutyProvinceCompanyName(String dutyProvinceCompanyName) {
        this.dutyProvinceCompanyName = dutyProvinceCompanyName;
    }

    public String getDutyProvinceCompanyCode() {
        return dutyProvinceCompanyCode;
    }

    public void setDutyProvinceCompanyCode(String dutyProvinceCompanyCode) {
        this.dutyProvinceCompanyCode = dutyProvinceCompanyCode;
    }

    public String getDutyAreaCode() {
        return dutyAreaCode;
    }

    public void setDutyAreaCode(String dutyAreaCode) {
        this.dutyAreaCode = dutyAreaCode;
    }

    public String getDutyAreaName() {
        return dutyAreaName;
    }

    public void setDutyAreaName(String dutyAreaName) {
        this.dutyAreaName = dutyAreaName;
    }

    public String getDutyOrgCode() {
        return dutyOrgCode;
    }

    public void setDutyOrgCode(String dutyOrgCode) {
        this.dutyOrgCode = dutyOrgCode;
    }

    public String getDutyOrgName() {
        return dutyOrgName;
    }

    public void setDutyOrgName(String dutyOrgName) {
        this.dutyOrgName = dutyOrgName;
    }

    public String getDutyStaffAccount() {
        return dutyStaffAccount;
    }

    public void setDutyStaffAccount(String dutyStaffAccount) {
        this.dutyStaffAccount = dutyStaffAccount;
    }

    public Integer getDutyStaffType() {
        return dutyStaffType;
    }

    public void setDutyStaffType(Integer dutyStaffType) {
        this.dutyStaffType = dutyStaffType;
    }

    public String getDutyStaffName() {
        return dutyStaffName;
    }

    public void setDutyStaffName(String dutyStaffName) {
        this.dutyStaffName = dutyStaffName;
    }

    public String getReConfirmLong() {
        return reConfirmLong;
    }

    public void setReConfirmLong(String reConfirmLong) {
        this.reConfirmLong = reConfirmLong;
    }

    public String getReConfirmWidth() {
        return reConfirmWidth;
    }

    public void setReConfirmWidth(String reConfirmWidth) {
        this.reConfirmWidth = reConfirmWidth;
    }

    public String getReConfirmHigh() {
        return reConfirmHigh;
    }

    public void setReConfirmHigh(String reConfirmHigh) {
        this.reConfirmHigh = reConfirmHigh;
    }

    public String getStartStaffAccount() {
        return startStaffAccount;
    }

    public void setStartStaffAccount(String startStaffAccount) {
        this.startStaffAccount = startStaffAccount;
    }

    public Integer getStartStaffType() {
        return startStaffType;
    }

    public void setStartStaffType(Integer startStaffType) {
        this.startStaffType = startStaffType;
    }

    public String getStartStaffName() {
        return startStaffName;
    }

    public void setStartStaffName(String startStaffName) {
        this.startStaffName = startStaffName;
    }

    public String getStartRegion() {
        return startRegion;
    }

    public void setStartRegion(String startRegion) {
        this.startRegion = startRegion;
    }

    public String getStartRegionCode() {
        return startRegionCode;
    }

    public void setStartRegionCode(String startRegionCode) {
        this.startRegionCode = startRegionCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getConfirmWeight() {
        return confirmWeight;
    }

    public void setConfirmWeight(String confirmWeight) {
        this.confirmWeight = confirmWeight;
    }

    public String getConfirmVolume() {
        return confirmVolume;
    }

    public void setConfirmVolume(String confirmVolume) {
        this.confirmVolume = confirmVolume;
    }

    public String getReConfirmWeight() {
        return reConfirmWeight;
    }

    public void setReConfirmWeight(String reConfirmWeight) {
        this.reConfirmWeight = reConfirmWeight;
    }

    public String getReConfirmVolume() {
        return reConfirmVolume;
    }

    public void setReConfirmVolume(String reConfirmVolume) {
        this.reConfirmVolume = reConfirmVolume;
    }

    public String getConvertCoefficient() {
        return convertCoefficient;
    }

    public void setConvertCoefficient(String convertCoefficient) {
        this.convertCoefficient = convertCoefficient;
    }

    public String getDiffWeight() {
        return diffWeight;
    }

    public void setDiffWeight(String diffWeight) {
        this.diffWeight = diffWeight;
    }

    public String getStanderDiff() {
        return standerDiff;
    }

    public void setStanderDiff(String standerDiff) {
        this.standerDiff = standerDiff;
    }

    public Integer getExceedType() {
        return exceedType;
    }

    public void setExceedType(Integer exceedType) {
        this.exceedType = exceedType;
    }

    public Integer getConfirmWeightSource() {
        return confirmWeightSource;
    }

    public void setConfirmWeightSource(Integer confirmWeightSource) {
        this.confirmWeightSource = confirmWeightSource;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Date statusUpdateTime) {
        this.statusUpdateTime = statusUpdateTime;
    }

    public Integer getAppendix() {
        return appendix;
    }

    public void setAppendix(Integer appendix) {
        this.appendix = appendix;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
