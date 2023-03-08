package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.Context;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.util.ArrayList;
import java.util.List;


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
	 * 运单接口查询到的旧单的运单信息，如果有旧单的话 使用前请判断NULL
     */
	private BigWaybillDto oldBigWaybillDto;
	/**
	 * 封装后的运单信息
	 */
	private Waybill waybill;

	/**
	 * 商家id
	 * */
	private String busiCode;

	/**
	 * 商家标识位
	 * */
	private String traderSign;

	/**
	 * 获取waybillSign信息
	 * @return
	 */
	public String getWaybillSign(){
		if(bigWaybillDto != null && bigWaybillDto.getWaybill() != null){
			return bigWaybillDto.getWaybill().getWaybillSign();
		}
		return null;
	}
	/**
	 * 获取SendPay信息
	 * @return
	 */
	public String getSendPay(){
		if(bigWaybillDto != null && bigWaybillDto.getWaybill() != null){
			return bigWaybillDto.getWaybill().getSendPay();
		}
		return null;
	}
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
	/**
	 * 站点平台打印-打印结果数据
	 */
	private LabelPrintingResponse labelPrintingResponse;

	/**
	 * 运单是否交接完成
	 */
	private Boolean isCollectComplete;

	/**
	 * 操作人所属场地是否为分拣中心
	 */
    private Boolean dmsCenter=Boolean.FALSE;

	/**
	 * 操作人所属场地是否为第三方
	 */
	private Boolean isThirdPartner = Boolean.FALSE;

	/**
	 * 操作人所属站点是否为营业部
	 */
	private Boolean isBusinessDepartment = Boolean.FALSE;

	/**
     * 是否使用目的分拣中心,获取滑道信息时设置
     */
    private boolean isUseEndDmsId = false;
    /**
     * 运单中的目的分拣中心,获取滑道信息时设置
     */
    private Integer waybillEndDmsId;
    
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

	public BigWaybillDto getOldBigWaybillDto() {
		return oldBigWaybillDto;
	}

	public void setOldBigWaybillDto(BigWaybillDto oldBigWaybillDto) {
		this.oldBigWaybillDto = oldBigWaybillDto;
	}

	/**
	 * @return the labelPrintingResponse
	 */
	public LabelPrintingResponse getLabelPrintingResponse() {
		return labelPrintingResponse;
	}

	/**
	 * @param labelPrintingResponse the labelPrintingResponse to set
	 */
	public void setLabelPrintingResponse(LabelPrintingResponse labelPrintingResponse) {
		this.labelPrintingResponse = labelPrintingResponse;
	}

	public String getTraderSign() {
		return traderSign;
	}

	public void setTraderSign(String traderSign) {
		this.traderSign = traderSign;
	}

	public Boolean getCollectComplete() {
		return isCollectComplete;
	}

	public void setCollectComplete(Boolean collectComplete) {
		isCollectComplete = collectComplete;
	}

	public Boolean getDmsCenter() {
		return dmsCenter;
	}

	public void setDmsCenter(Boolean dmsCenter) {
		this.dmsCenter = dmsCenter;
	}
	public boolean isUseEndDmsId() {
		return isUseEndDmsId;
	}
	public void setUseEndDmsId(boolean isUseEndDmsId) {
		this.isUseEndDmsId = isUseEndDmsId;
	}
	public Integer getWaybillEndDmsId() {
		return waybillEndDmsId;
	}
	public void setWaybillEndDmsId(Integer waybillEndDmsId) {
		this.waybillEndDmsId = waybillEndDmsId;
	}

	public Boolean getThirdPartner() {
		return isThirdPartner;
	}

	public void setThirdPartner(Boolean thirdPartner) {
		isThirdPartner = thirdPartner;
	}

	public Boolean getBusinessDepartment() {
		return isBusinessDepartment;
	}

	public void setBusinessDepartment(Boolean businessDepartment) {
		isBusinessDepartment = businessDepartment;
	}
}
