package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

/**
 * @ClassName AppUpgradeResponse
 * @Description
 * @Author wyh
 * @Date 2022/3/12 14:21
 **/
public class AppUpgradeResponse implements Serializable {

    private static final long serialVersionUID = 3770170155850670920L;

    private Boolean needUpdate;

    private String versionCode;

    private Integer downloadType;

    private String downloadUrl;

    private String fileName;

    private Long fileSize = 0L;

    private String fileCheckCode;

    private String versionRemark;

    private String fileItemsCheckCode;

    private String runningMode;

    public Boolean getNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(Boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(Integer downloadType) {
        this.downloadType = downloadType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileCheckCode() {
        return fileCheckCode;
    }

    public void setFileCheckCode(String fileCheckCode) {
        this.fileCheckCode = fileCheckCode;
    }

    public String getVersionRemark() {
        return versionRemark;
    }

    public void setVersionRemark(String versionRemark) {
        this.versionRemark = versionRemark;
    }

    public String getFileItemsCheckCode() {
        return fileItemsCheckCode;
    }

    public void setFileItemsCheckCode(String fileItemsCheckCode) {
        this.fileItemsCheckCode = fileItemsCheckCode;
    }

    public String getRunningMode() {
        return runningMode;
    }

    public void setRunningMode(String runningMode) {
        this.runningMode = runningMode;
    }
}
