package com.jd.bluedragon.distribution.api.response;



public class SysConfigContentResponse {

	private static final long serialVersionUID = 1422606529312362690L;



	public static final Integer CODE_OK = 200;
	public static final String MESSAGE_OK = "OK";

	public static final Integer CODE_OK_NULL = 2200;
	public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";

	public static final Integer CODE_SERVICE_ERROR = 20000;
	public static final String MESSAGE_SERVICE_ERROR = "服务异常";


	/**配置信息*/
	private String configContent;



	/** 相应状态码 */
	private Integer code;

	/** 响应消息 */
	private String message;

	public String getConfigContent() {
		return configContent;
	}

	public SysConfigContentResponse() {
	}

	public SysConfigContentResponse(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public SysConfigContentResponse(Integer code, String message,String configContent) {
		this.code = code;
		this.message = message;
		this.configContent = configContent;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public void setConfigContent(String configContent) {
		this.configContent = configContent;
	}

}
