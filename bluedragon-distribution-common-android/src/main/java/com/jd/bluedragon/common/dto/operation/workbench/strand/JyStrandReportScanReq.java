package com.jd.bluedragon.common.dto.operation.workbench.strand;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandScanTypeEnum;

import java.io.Serializable;

/**
 * 拣运app-滞留上报扫描请求体
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportScanReq implements Serializable {

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
     * 岗位码
     */
    private String positionCode;

    /**
     * 操作人ERP
     */
    private String operateUserErp;
    
    /**
     * 扫描条码
     */
    private String scanBarCode;

    /**
     * 扫描条码归属容器
     */
    private String containerCode;

    /**
     * 扫描类型
     * @see JyBizStrandScanTypeEnum
     */
    private Integer scanType;

    private Integer taskStatus;

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

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getScanBarCode() {
        return scanBarCode;
    }

    public void setScanBarCode(String scanBarCode) {
        this.scanBarCode = scanBarCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
}
