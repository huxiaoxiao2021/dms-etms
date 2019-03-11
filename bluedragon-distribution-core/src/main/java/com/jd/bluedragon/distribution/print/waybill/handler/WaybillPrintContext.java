package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Context;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;


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
	
	private InterceptResult<String> result;
	/**
	 * 运单接口查询到的运单信息
	 */
	private BigWaybillDto bigWaybillDto;
	/**
	 * 封装后的运单信息
	 */
	private Waybill waybill;

	/**
	 * 商家id
	 * */
	private String busiCode;

	public String getBusiCode() {
		return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}

	/**
	 * 记录全局状态
	 */
	private int status = InterceptResult.STATUS_PASSED;
	
	private	List<String> messages = new ArrayList<String>();

	/**
	 * 运单打印--基础类型
	 */
	private BasePrintWaybill basePrintWaybill;
	
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
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * 设置全局状态，状态值大于当前值时才会覆盖
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		if(status>this.status){
			this.status = status;
		}
	}

    public BigWaybillDto getBigWaybillDto() {
        return bigWaybillDto;
    }

    public void setBigWaybillDto(BigWaybillDto bigWaybillDto) {
        this.bigWaybillDto = bigWaybillDto;
    }

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }
	/**
	 * @return the result
	 */
	public InterceptResult<String> getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(InterceptResult<String> result) {
		this.result = result;
	}

	public BasePrintWaybill getBasePrintWaybill() {
		return basePrintWaybill;
	}

	public void setBasePrintWaybill(BasePrintWaybill basePrintWaybill) {
		this.basePrintWaybill = basePrintWaybill;
	}
}
