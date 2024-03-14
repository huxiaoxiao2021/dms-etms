package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request;

import java.io.Serializable;

/**
 * 验货扫描接口
 *
 * @author fanggang7
 * @time 2022-10-09 14:31:03 周日
 */
public class InspectionScanRequest extends InspectionCommonRequest implements Serializable {

    private static final long serialVersionUID = 4648667337916471503L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 扫描方式
     */
    private Integer scanType;

    /**
     * 跳过拦截强制提交
     */
    private Boolean forceSubmit;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public Boolean getForceSubmit() {
        return forceSubmit;
    }

    public void setForceSubmit(Boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }
}
