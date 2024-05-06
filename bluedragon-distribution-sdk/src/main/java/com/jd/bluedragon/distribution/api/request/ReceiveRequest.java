package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.OperatorData;

public class ReceiveRequest extends JdRequest{
	private static final long serialVersionUID = 8200718370214764985L;

	/** 箱号或者包裹号 */
	private String packOrBox;
	/** 封车号 */
	private String shieldsCarCode;
	/**
	 * 扫描封车时间
	 */
	private String shieldsCarTime;
	/** 车号 */
	private String carCode;
    /**周转箱号*/
	private String turnoverBoxCode;
	  /**封箱号*/
    private String sealBoxCode;
     /**排队号*/
    private String queueNo; 
    /**
     * 发车号
     */
	@Deprecated
    private String departureCarId;
	/**
	 * 操作信息对象
	 */
	private OperatorData operatorData;

	/**
	 * 操作信息对象json格式
	 */
	private String operatorDataJson;

	/**
	 *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
	 * 操作者类型编码
	 */
	private Integer operatorTypeCode;

	/**
	 * 操作者id
	 */
	private String operatorId;
    
    public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public String getSealBoxCode() {
		return sealBoxCode;
	}

	public void setSealBoxCode(String sealBoxCode) {
		this.sealBoxCode = sealBoxCode;
	}

	public String getTurnoverBoxCode() {
		return turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

	public String getPackOrBox() {
		return packOrBox;
	}

	public void setPackOrBox(String packOrBox) {
		this.packOrBox = packOrBox;
	}

	public String getShieldsCarCode() {
		return shieldsCarCode;
	}

	public void setShieldsCarCode(String shieldsCarCode) {
		this.shieldsCarCode = shieldsCarCode;
	}

	public String getShieldsCarTime() {
		return shieldsCarTime;
	}

	public void setShieldsCarTime(String shieldsCarTime) {
		this.shieldsCarTime = shieldsCarTime;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public String getDepartureCarId() {
		return departureCarId;
	}

	public void setDepartureCarId(String departureCarId) {
		this.departureCarId = departureCarId;
	}

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}

	public String getOperatorDataJson() {
		return operatorDataJson;
	}

	public void setOperatorDataJson(String operatorDataJson) {
		this.operatorDataJson = operatorDataJson;
	}

	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}

	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
}
