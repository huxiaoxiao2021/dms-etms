package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 
 * @ClassName: JdCloudPrintOssConfig
 * @Description: 云打印Oss配置
 * @author: wuyoude
 * @date: 2019年8月14日 下午4:32:18
 *
 */
public class JdCloudPrintOssConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * OSS 应用的accessKey
	 */
	private String accessKey;
	/**
	 * OSS应用的 secretkey
	 */
	private String secretKey;
	/**
	 * OSS服务域名，可将文件存储到测试或线上环境的oss，1）oss测试环境：test.storage.jd.com；2）oss线上环境：storage.jd.local
	 */
	private String endpoint;
	/**
	 * OSS 应用的Bucket空间名称
	 */
	private String bucket;
	/**
	 * 超时设置单位ms，默认30000
	 */
	private Integer socketTimeout = 30000;
	/**
	 * @return the accessKey
	 */
	public String getAccessKey() {
		return accessKey;
	}
	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}
	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	/**
	 * @return the bucket
	 */
	public String getBucket() {
		return bucket;
	}
	/**
	 * @param bucket the bucket to set
	 */
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	/**
	 * @return the socketTimeout
	 */
	public Integer getSocketTimeout() {
		return socketTimeout;
	}
	/**
	 * @param socketTimeout the socketTimeout to set
	 */
	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}
	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}
