package com.jd.bluedragon.common.dto.operation.workbench.strand;

import java.io.Serializable;
import java.util.List;

/**
 * 拣运app-滞留上报任务明细VO
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportTaskDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;
    
    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 任务创建人ERP
     */
    private String createUserErp;

    /**
     * 任务创建人名称
     */
    private String createUserName;

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
     * 证明照片
     */
    private List<String> proveUrlList;

    /**
     * 已扫明细（默认只查询第一页内容）
     */
    private List<JyStrandReportScanVO> scanVOList;

    /**
     * 扫描数量
     */
    private Integer scanNum;

    /**
     * 提交时间
     */
    private Long submitTime;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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

    public List<JyStrandReportScanVO> getScanVOList() {
        return scanVOList;
    }

    public void setScanVOList(List<JyStrandReportScanVO> scanVOList) {
        this.scanVOList = scanVOList;
    }

    public Integer getScanNum() {
        return scanNum;
    }

    public void setScanNum(Integer scanNum) {
        this.scanNum = scanNum;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }
}
