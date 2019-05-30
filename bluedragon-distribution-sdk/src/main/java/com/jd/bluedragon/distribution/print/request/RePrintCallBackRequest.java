package com.jd.bluedragon.distribution.print.request;

import com.jd.ql.dms.common.domain.JdRequest;

/**
 * 
 * @ClassName: RePrintCallBackRequest
 * @Description: 包裹补打回调-请求
 * @author: wuyoude
 * @date: 2019年4月22日 下午4:25:24
 *
 */
public class RePrintCallBackRequest extends JdRequest{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6603963713619416807L;
	/**
	 * 运单号
	 */
	private String waybillCode;
	/**
	 * 包裹号
	 */
	private String packageCode;
	/**
	 * waybillSign
	 */
	private String waybillSign;
	
	/**
	 * 打印结果-模板分组(区分B网和C网),TemplateGroupEnum对应的code
	 */
	private String templateGroupCode;
	/**
	 * 模板名称
	 */
	private String templateName;
	/**
	 * 模板版本-默认为0，最后一个版本号
	 */
	private Integer templateVersion = 0;
	/**
	 * 用户erp账号
	 */
	private String userErp;
	/**
	 * @return the waybillCode
	 */
	public String getWaybillCode() {
		return waybillCode;
	}
	/**
	 * @param waybillCode the waybillCode to set
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	/**
	 * @return the packageCode
	 */
	public String getPackageCode() {
		return packageCode;
	}
	/**
	 * @param packageCode the packageCode to set
	 */
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	/**
	 * @return the waybillSign
	 */
	public String getWaybillSign() {
		return waybillSign;
	}
	/**
	 * @param waybillSign the waybillSign to set
	 */
	public void setWaybillSign(String waybillSign) {
		this.waybillSign = waybillSign;
	}
	/**
	 * @return the templateGroupCode
	 */
	public String getTemplateGroupCode() {
		return templateGroupCode;
	}
	/**
	 * @param templateGroupCode the templateGroupCode to set
	 */
	public void setTemplateGroupCode(String templateGroupCode) {
		this.templateGroupCode = templateGroupCode;
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
	/**
	 * @return the userErp
	 */
	public String getUserErp() {
		return userErp;
	}
	/**
	 * @param userErp the userErp to set
	 */
	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}
}
