package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

public class FileMetadata implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 最后修改时间
	 */
	private Long lastModified;
	/**
	 * 最后修改时间
	 */
	private String contentMd5;
	/**
	 * 是否存在标识
	 */
	private Boolean isExists;
	/**
	 * 判断文件是否存在
	 * @param metadata
	 * @return
	 */
	public static boolean checkExists(FileMetadata metadata) {
		return (metadata != null && Boolean.TRUE.equals(metadata.getIsExists()));
	}
	/**
	 * 判断文件是否需要下载
	 * @param loacal 本地文件metadata
	 * @param remote 远程文件metadata
	 * @return
	 */
	public static boolean needDownload(FileMetadata loacal,FileMetadata remote) {
		//远程文件不存在，无需下载
		if(!checkExists(remote)) {
			return false;
		}
		//本地文件不存在，需要下载
		if(!checkExists(loacal)) {
			return true;
		}
		//同时存在时，比较文件修改时间，不一致则需要下载
		if(remote.lastModified != null 
				&& remote.lastModified.equals(loacal.lastModified)
				&& remote.contentMd5 != null 
				&& remote.contentMd5.equals(loacal.contentMd5)) {
			return false;
		}
		return true;
	}
	public Long getLastModified() {
		return lastModified;
	}
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}
	public Boolean getIsExists() {
		return isExists;
	}
	public void setIsExists(Boolean isExists) {
		this.isExists = isExists;
	}
	public String getContentMd5() {
		return contentMd5;
	}
	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}
}
