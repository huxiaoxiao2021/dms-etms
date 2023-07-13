package com.jd.bluedragon.common.dto.operation.workbench.strand;

import java.io.Serializable;

/**
 * 拣运app-滞留上报任务明细VO
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportScanVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 扫货方式
     */
    private Integer scanType;

    /**
     * 已扫条码
     */
    private String scanBarCode;

    /**
     * 扫描条码对应容器
     */
    private String containerCode;
    
    /**
     * 容器内件数
     */
    private Integer containerInnerCount;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
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

    public Integer getContainerInnerCount() {
        return containerInnerCount;
    }

    public void setContainerInnerCount(Integer containerInnerCount) {
        this.containerInnerCount = containerInnerCount;
    }
}
