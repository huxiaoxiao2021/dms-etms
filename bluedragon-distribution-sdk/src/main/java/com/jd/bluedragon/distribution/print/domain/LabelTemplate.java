package com.jd.bluedragon.distribution.print.domain;


/**
 * 
 * @ClassName: LabelTemplate
 * @Description: 标签模板
 * @author: wuyoude
 * @date: 2018年7月5日 上午10:28:44
 *
 */
public class LabelTemplate implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Id
	 */
	private Long id;
	/**
	 * 模板名
	 */
	private String templateName;
	/**
	 * 模板版本
	 */
	private Integer templateVersion;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}
	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	/**
	 * @return the templateVersion
	 */
	public Integer getTemplateVersion() {
		return templateVersion;
	}
	/**
	 * @param templateVersion the templateVersion to set
	 */
	public void setTemplateVersion(Integer templateVersion) {
		this.templateVersion = templateVersion;
	}
}