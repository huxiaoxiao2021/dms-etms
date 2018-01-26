package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.Context;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.StringHelper;


/**
 * 
 * @ClassName: WaybillPrintContext
 * @Description: 打印上下文信息
 * @author: wuyoude
 * @date: 2018年1月25日 下午10:50:21
 *
 */
public class WaybillPrintContext implements Context{
	/**
	 * 请求对象
	 */
	private WaybillPrintRequest request;
	/**
	 * 请求响应
	 */
	private WaybillPrintResponse response;
	/**
	 * 运单信息
	 */
	private PrintWaybill printWaybill;
	
	private	List<String> messages = new ArrayList<String>();
	
	public void appendMessage(String message){
		if(StringHelper.isNotEmpty(message)){
			messages.add(message);
		}
	}
	/**
	 * @return the request
	 */
	public WaybillPrintRequest getRequest() {
		return request;
	}
	/**
	 * @param request the request to set
	 */
	public void setRequest(WaybillPrintRequest request) {
		this.request = request;
	}
	/**
	 * @return the response
	 */
	public WaybillPrintResponse getResponse() {
		return response;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(WaybillPrintResponse response) {
		this.response = response;
	}
	/**
	 * @return the printWaybill
	 */
	public PrintWaybill getPrintWaybill() {
		return printWaybill;
	}
	/**
	 * @param printWaybill the printWaybill to set
	 */
	public void setPrintWaybill(PrintWaybill printWaybill) {
		this.printWaybill = printWaybill;
	}
	/**
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
