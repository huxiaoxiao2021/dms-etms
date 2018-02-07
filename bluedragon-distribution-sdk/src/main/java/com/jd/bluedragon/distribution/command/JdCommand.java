package com.jd.bluedragon.distribution.command;

import java.io.Serializable;

/**
 * 
 * @ClassName: JdCommand
 * @Description: 通用命令请求
 * @author: wuyoude
 * @date: 2018年1月29日 上午10:22:40
 * 
 * @param <T> 请求实体类型
 */
public class JdCommand<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
     *  请求应用程序类型-40-青龙打印客户端
     */
	protected Integer programType;
    /**
	 * 应用程序版本号:20180104WM
     */
    protected String versionCode;
	/**
	 * 业务类型
	 */
    protected Integer businessType;
	/**
	 * 业务操作类型
	 */
    protected Integer operateType;
	/**
	 * 请求实体
	 */
	protected T data;
	/**
	 * @return the programType
	 */
	public Integer getProgramType() {
		return programType;
	}
	/**
	 * @param programType the programType to set
	 */
	public void setProgramType(Integer programType) {
		this.programType = programType;
	}
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
	 * @return the businessType
	 */
	public Integer getBusinessType() {
		return businessType;
	}
	/**
	 * @param businessType the businessType to set
	 */
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}
	/**
	 * @return the operateType
	 */
	public Integer getOperateType() {
		return operateType;
	}
	/**
	 * @param operateType the operateType to set
	 */
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}
	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}
}
