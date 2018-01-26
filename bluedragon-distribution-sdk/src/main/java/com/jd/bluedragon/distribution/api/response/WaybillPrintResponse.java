package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

/**
 * 
 * @ClassName: WaybillPrintResponse
 * @Description: 包裹标签打印请求返回实体
 * @author: wuyoude
 * @date: 2018年1月25日 下午4:44:43
 *
 */
public class WaybillPrintResponse extends PrintWaybill{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 模板名称
	 */
	private String templateName;
	/**
	 * 模板版本-默认为1
	 */
	private int templateVersion = 1;
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
	public int getTemplateVersion() {
		return templateVersion;
	}
	/**
	 * @param templateVersion the templateVersion to set
	 */
	public void setTemplateVersion(int templateVersion) {
		this.templateVersion = templateVersion;
	}
	
}
