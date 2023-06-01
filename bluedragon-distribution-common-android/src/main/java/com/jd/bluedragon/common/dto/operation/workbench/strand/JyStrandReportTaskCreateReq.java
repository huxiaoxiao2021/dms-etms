package com.jd.bluedragon.common.dto.operation.workbench.strand;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * 拣运app-滞留上报任务创建请求体
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportTaskCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 运输驳回唯一键
     */
    private String transportRejectBiz;
    /**
     * 波次时间
     */
    private String waveTime;
    
    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人名称
     */
    private String operateUserName;

    /**
     * 滞留原因编码
     */
    private Integer strandCode;

    /**
     * 滞留原因
     */
    private String strandReason;

    /**
     * 下级流向场地编码
     */
    private Integer nextSiteCode;

    /**
     * 下级流向场地名称
     */
    private String nextSiteName;

    /**
     * 证明照片（多个以,隔开）
     */
    private List<String> proveUrlList;

    /**
     * 任务类型
     * @see JyBizStrandTaskTypeEnum
     */
    private Integer taskType;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTransportRejectBiz() {
        return transportRejectBiz;
    }

    public void setTransportRejectBiz(String transportRejectBiz) {
        this.transportRejectBiz = transportRejectBiz;
    }

    public String getWaveTime() {
        return waveTime;
    }

    public void setWaveTime(String waveTime) {
        this.waveTime = waveTime;
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

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Integer getStrandCode() {
        return strandCode;
    }

    public void setStrandCode(Integer strandCode) {
        this.strandCode = strandCode;
    }

    public String getStrandReason() {
        return strandReason;
    }

    public void setStrandReason(String strandReason) {
        this.strandReason = strandReason;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public List<String> getProveUrlList() {
        return proveUrlList;
    }

    public void setProveUrlList(List<String> proveUrlList) {
        this.proveUrlList = proveUrlList;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
