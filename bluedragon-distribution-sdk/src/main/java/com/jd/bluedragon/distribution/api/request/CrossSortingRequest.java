package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdObject;

public class CrossSortingRequest extends JdObject {

	private static final long serialVersionUID = 3535200784092294676L;

	/** 主键ID */
	private Long id;

	/** 建包/发货区域ID */
	private Integer orgId;

	/** 建包/发货分拣中心编码 */
	private Integer createDmsCode;

	/** 建包/发货分拣中心 */
	private String createDmsName;

	/** 目的分拣中心编码 */
	private Integer destinationDmsCode;

	/** 目的分拣中心 */
	private String destinationDmsName;

	/** 可混装分拣中心ID */
	private Integer mixDmsCode;

	/** 可混装分拣中心 */
	private String mixDmsName;

	/** 维护人姓名 */
	private String createUserName;

	/** 规则类型 */
	private Integer type;

	/** 批量请求 */
	private String data;
	/**
	 * 运输类型
	 */
	private Integer transportType;

	public Integer getTransportType() {
		return transportType;
	}

	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getCreateDmsCode() {
		return createDmsCode;
	}

	public void setCreateDmsCode(Integer createDmsCode) {
		this.createDmsCode = createDmsCode;
	}

	public Integer getDestinationDmsCode() {
		return destinationDmsCode;
	}

	public void setDestinationDmsCode(Integer destinationDmsCode) {
		this.destinationDmsCode = destinationDmsCode;
	}

	public Integer getMixDmsCode() {
		return mixDmsCode;
	}

	public void setMixDmsCode(Integer mixDmsCode) {
		this.mixDmsCode = mixDmsCode;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCreateDmsName() {
		return createDmsName;
	}

	public void setCreateDmsName(String createDmsName) {
		this.createDmsName = createDmsName;
	}

	public String getDestinationDmsName() {
		return destinationDmsName;
	}

	public void setDestinationDmsName(String destinationDmsName) {
		this.destinationDmsName = destinationDmsName;
	}

	public String getMixDmsName() {
		return mixDmsName;
	}

	public void setMixDmsName(String mixDmsName) {
		this.mixDmsName = mixDmsName;
	}

}
