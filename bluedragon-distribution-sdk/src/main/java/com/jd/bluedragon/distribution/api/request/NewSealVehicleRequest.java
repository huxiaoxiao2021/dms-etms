package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;

import java.util.List;

/**
 * create by zhanglei 2017-05-10
 * 增加运力编码、批次、重量、体积及封车号的多层级关系
 */
public class NewSealVehicleRequest extends JdRequest {

	private static final long serialVersionUID = -4900034488418821323L;

	/**
	 * 代码
	 * */
	private Integer code;

	/**
	 * 信息
	 * */
	private String message;

	/**
	 * 10:封车
	 * 20:解封车
	 * */
	private Integer status;

	/**
	 * 显示条数
	 * */
	private Integer pageNums;

	/**
	 * 封车号
	 * */
	private String sealCode;

	/**
	 * 车牌号
	 * */
	private String vehicleNumber;

	/**
	 *
	 * */
	private String transportCode;

	/**
	 * 始发站点
	 * */
	private String startSiteId;

	/**
	 * 目的站点s
	 * */
	private String endSiteId;


	/**批次基本信息*/
	private List<SealCarDto> data;

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPageNums() {
		return pageNums;
	}

	public void setPageNums(Integer pageNums) {
		this.pageNums = pageNums;
	}

	public String getSealCode() {
		return sealCode;
	}

	public void setSealCode(String sealCode) {
		this.sealCode = sealCode;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public String getStartSiteId() {
		return startSiteId;
	}

	public void setStartSiteId(String startSiteId) {
		this.startSiteId = startSiteId;
	}

	public String getEndSiteId() {
		return endSiteId;
	}

	public void setEndSiteId(String endSiteId) {
		this.endSiteId = endSiteId;
	}

	public List<SealCarDto> getData() {
		return data;
	}

	public void setData(List<SealCarDto> data) {
		this.data = data;
	}
}
