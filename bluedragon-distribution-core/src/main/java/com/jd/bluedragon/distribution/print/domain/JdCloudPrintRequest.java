package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName: JdCloudPrintRequest
 * @Description: 云打印请求体
 * @author: wuyoude
 * @date: 2019年8月14日 下午4:52:40
 * 
 * @param <M> 打印请求数据模型
 */
public class JdCloudPrintRequest<M> implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 系统标识
	 */
	private String sys;
	/**
	 * 模板编码
	 */
	private String template;
	/**
	 * 模板版本
	 */
	private String templateVer;
	/**
	 * 打印时间
	 */
	private Long time;
	/**
	 * 意图：定位到是哪个仓、场地的打印作业
	 */
	private String location;
	/**
	 * 用户
	 */
	private String user;
	/**
	 * 单号
	 */
	private String orderNum;
	/**
	 * 输出配置
	 */
	private List<JdCloudPrintOutputConfig> outputConfig;
	/**
	 * 打印数据模型列表
	 */
	private List<M> model;
	/**
	 * @return the sys
	 */
	public String getSys() {
		return sys;
	}
	/**
	 * @param sys the sys to set
	 */
	public void setSys(String sys) {
		this.sys = sys;
	}
	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}
	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	/**
	 * @return the templateVer
	 */
	public String getTemplateVer() {
		return templateVer;
	}
	/**
	 * @param templateVer the templateVer to set
	 */
	public void setTemplateVer(String templateVer) {
		this.templateVer = templateVer;
	}
	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the orderNum
	 */
	public String getOrderNum() {
		return orderNum;
	}
	/**
	 * @param orderNum the orderNum to set
	 */
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	/**
	 * @return the outputConfig
	 */
	public List<JdCloudPrintOutputConfig> getOutputConfig() {
		return outputConfig;
	}
	/**
	 * @param outputConfig the outputConfig to set
	 */
	public void setOutputConfig(List<JdCloudPrintOutputConfig> outputConfig) {
		this.outputConfig = outputConfig;
	}
	/**
	 * @return the model
	 */
	public List<M> getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(List<M> model) {
		this.model = model;
	}
}
