package com.jd.bluedragon.core.jmq.domain;

import java.util.Date;
import java.util.List;
/**
 * 封车mq消息体
 * @ClassName: SealCarMqDto
 * @Description: TODO
 * @author: wuyoude
 * @date: 2017年9月28日 下午2:53:36
 *
 */
public class SealCarMqDto{
	/**
	 * 操作人分拣中心id
	 */
	private Integer dmsSiteId;
	/**
	 * 车牌号
	 */
	private String vehicleNumber;
	/**
	 * 运力编码
	 */
	private String transportCode;
	/**
	 * 操作人编号
	 */
	private String operUserCode;
	/**
	 * 操作人名称
	 */
	private String operUserName;
	/**
	 * 操作时间
	 */
	private Date operTime;
	/**
	 * 封签号列表
	 */
	private List<String> sealCodes;
	/**
	 * 批次号列表
	 */
	private List<String> sendCodeList;

	/**
	 * 封车编码
	 */
	private String sealCarCode;


	public String getSealCarCode() {
		return sealCarCode;
	}

	public void setSealCarCode(String sealCarCode) {
		this.sealCarCode = sealCarCode;
	}

	/**
	 * @return the dmsSiteId
	 */
	public Integer getDmsSiteId() {
		return dmsSiteId;
	}

	/**
	 * @param dmsSiteId the dmsSiteId to set
	 */
	public void setDmsSiteId(Integer dmsSiteId) {
		this.dmsSiteId = dmsSiteId;
	}
	/**
	 * @return the vehicleNumber
	 */
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	/**
	 * @param vehicleNumber the vehicleNumber to set
	 */
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	/**
	 * @return the transportCode
	 */
	public String getTransportCode() {
		return transportCode;
	}

	/**
	 * @param transportCode the transportCode to set
	 */
	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	/**
	 * @return the operUserCode
	 */
	public String getOperUserCode() {
		return operUserCode;
	}

	/**
	 * @param operUserCode the operUserCode to set
	 */
	public void setOperUserCode(String operUserCode) {
		this.operUserCode = operUserCode;
	}

	/**
	 * @return the operUserName
	 */
	public String getOperUserName() {
		return operUserName;
	}

	/**
	 * @param operUserName the operUserName to set
	 */
	public void setOperUserName(String operUserName) {
		this.operUserName = operUserName;
	}

	/**
	 * @return the operTime
	 */
	public Date getOperTime() {
		return operTime;
	}

	/**
	 * @param operTime the operTime to set
	 */
	public void setOperTime(Date operTime) {
		this.operTime = operTime;
	}

	/**
	 * @return the sendCodeList
	 */
	public List<String> getSendCodeList() {
		return sendCodeList;
	}

	/**
	 * @param sendCodeList the sendCodeList to set
	 */
	public void setSendCodeList(List<String> sendCodeList) {
		this.sendCodeList = sendCodeList;
	}

	/**
	 * @return the sealCodes
	 */
	public List<String> getSealCodes() {
		return sealCodes;
	}

	/**
	 * @param sealCodes the sealCodes to set
	 */
	public void setSealCodes(List<String> sealCodes) {
		this.sealCodes = sealCodes;
	}
}
