package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 导入响应对象
 */
public class PackageWeightImportResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private int successCount;

    private int errorCount;

    private int warnCount;

    private int count;

    private List<PackageWeightVO> errorList;

    private List<PackageWeightVO> successList;

    private List<PackageWeightVO> warnList;

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

    public int getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(int warnCount) {
        this.warnCount = warnCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PackageWeightVO> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<PackageWeightVO> errorList) {
        this.errorList = errorList;
    }

    public List<PackageWeightVO> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<PackageWeightVO> successList) {
        this.successList = successList;
    }

    public List<PackageWeightVO> getWarnList() {
        return warnList;
    }

    public void setWarnList(List<PackageWeightVO> warnList) {
        this.warnList = warnList;
    }
}
