package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
/**
 * 
 * @ClassName: DmsClientConfigInfo
 * @Description: 客户端配置信息：版本号、运行环境
 * @author: wuyoude
 * @date: 2020年1月2日 下午5:47:41
 *
 */
public class DmsClientConfigInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/** 当前版本号 */
	private String versionCode;
	
	 /** 当前运行环境 */
	private String runningMode;
	
	 /** 下载方式 */
	private Integer downloadType;
	
	 /** 下载地址 */
	private String downloadUrl;
	
	 /** 下载文件名 */
	private String fileName;
	
	 /** 下载文件大小,字节数 */
	private Long fileSize = 0L;
	
	 /** 文件验证码 */
	private String fileCheckCode;
	
	 /** 版本说明  */
	private String versionRemark;
	 /** 文件验证码 */
	private String fileItemsCheckCode;
	/**
	 * @return the versionCode
	 */
	public String getVersionCode() {
		return versionCode;
	}

	/**
	 * @param versionCode the versionCode to set
	 */
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	/**
	 * @return the runningMode
	 */
	public String getRunningMode() {
		return runningMode;
	}

	/**
	 * @param runningMode the runningMode to set
	 */
	public void setRunningMode(String runningMode) {
		this.runningMode = runningMode;
	}

	/**
	 * @return the downloadType
	 */
	public Integer getDownloadType() {
		return downloadType;
	}

	/**
	 * @param downloadType the downloadType to set
	 */
	public void setDownloadType(Integer downloadType) {
		this.downloadType = downloadType;
	}

	/**
	 * @return the downloadUrl
	 */
	public String getDownloadUrl() {
		return downloadUrl;
	}

	/**
	 * @param downloadUrl the downloadUrl to set
	 */
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileSize
	 */
	public Long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the fileCheckCode
	 */
	public String getFileCheckCode() {
		return fileCheckCode;
	}

	/**
	 * @param fileCheckCode the fileCheckCode to set
	 */
	public void setFileCheckCode(String fileCheckCode) {
		this.fileCheckCode = fileCheckCode;
	}

	/**
	 * @return the versionRemark
	 */
	public String getVersionRemark() {
		return versionRemark;
	}

	/**
	 * @param versionRemark the versionRemark to set
	 */
	public void setVersionRemark(String versionRemark) {
		this.versionRemark = versionRemark;
	}

	/**
	 * @return the fileItemsCheckCode
	 */
	public String getFileItemsCheckCode() {
		return fileItemsCheckCode;
	}

	/**
	 * @param fileItemsCheckCode the fileItemsCheckCode to set
	 */
	public void setFileItemsCheckCode(String fileItemsCheckCode) {
		this.fileItemsCheckCode = fileItemsCheckCode;
	}
}
