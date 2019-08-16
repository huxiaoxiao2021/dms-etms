package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName: JdCloudPrintResponse
 * @Description: 云打印返回体
 * @author: wuyoude
 * @date: 2019年8月14日 下午2:28:41
 *
 */
public class JdCloudPrintResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 成功标识码
	 */
	public static final Integer STATUS_SUC = 0;
	/**
	 * 与后文outputConfig中的输出配置相对应，0(File，输出到本地文件)，1(Printer，输出到打印机)，2(OSS，输出到对象存储)
	 */
	private Integer outputType;
	/**
	 *  当单个输出方式输出了多个文件时（例如多页表单输出为PNG格式，每页对应一张PNG图片，最后输出多张图片，但如果输出为PDF，只会有一个PDF，这个PDF中有多页
	 */
	private List<String> outputMsg;
	/**
	 * 状态 0-成功 | 1-失败
	 */
	private Integer status;
	/**
	 * 错误码
	 */
	private Integer errCode;
	/**
	 * 错误信息
	 */
	private Integer errMsg;
	/**
	 * @return the outputType
	 */
	public Integer getOutputType() {
		return outputType;
	}
	/**
	 * @param outputType the outputType to set
	 */
	public void setOutputType(Integer outputType) {
		this.outputType = outputType;
	}
	/**
	 * @return the outputMsg
	 */
	public List<String> getOutputMsg() {
		return outputMsg;
	}
	/**
	 * @param outputMsg the outputMsg to set
	 */
	public void setOutputMsg(List<String> outputMsg) {
		this.outputMsg = outputMsg;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the errCode
	 */
	public Integer getErrCode() {
		return errCode;
	}
	/**
	 * @param errCode the errCode to set
	 */
	public void setErrCode(Integer errCode) {
		this.errCode = errCode;
	}
	/**
	 * @return the errMsg
	 */
	public Integer getErrMsg() {
		return errMsg;
	}
	/**
	 * @param errMsg the errMsg to set
	 */
	public void setErrMsg(Integer errMsg) {
		this.errMsg = errMsg;
	}
}
