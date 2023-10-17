package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

public class JdCloudPrintOutputMsgItem implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 路径
	 */
	private String path;
	/**
	 * url
	 */
	private String url;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
