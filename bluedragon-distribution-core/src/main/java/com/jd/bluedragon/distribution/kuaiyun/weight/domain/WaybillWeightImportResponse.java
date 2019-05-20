package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 导入响应对象
 */
public class WaybillWeightImportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private int successCount;

    private int errorCount;

    private int warnCount;

    private int count;

    private List<WaybillWeightVO> errorList;

    private List<WaybillWeightVO> successList;

    private List<WaybillWeightVO> warnList;

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<WaybillWeightVO> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<WaybillWeightVO> errorList) {
        this.errorList = errorList;
    }

    public List<WaybillWeightVO> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<WaybillWeightVO> successList) {
        this.successList = successList;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(int warnCount) {
        this.warnCount = warnCount;
    }

    public List<WaybillWeightVO> getWarnList() {
        return warnList;
    }

    public void setWarnList(List<WaybillWeightVO> warnList) {
        this.warnList = warnList;
    }
}
