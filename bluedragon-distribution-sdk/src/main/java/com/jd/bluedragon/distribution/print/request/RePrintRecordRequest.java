package com.jd.bluedragon.distribution.print.request;

import com.jd.ql.dms.common.domain.JdRequest;

/**
 * 
 * @ClassName: RePrintRecordRequest
 * @Description: 包裹补打记录-请求
 * @author: wuyoude
 * @date: 2019年4月22日 下午4:25:24
 *
 */
public class RePrintRecordRequest extends JdRequest{
	private static final long serialVersionUID = 1L;
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	/**
	 * 打印结果-模板分组(区分B网和C网),TemplateGroupEnum对应的code
	 */
	private String templateGroupCode;

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
	
}
