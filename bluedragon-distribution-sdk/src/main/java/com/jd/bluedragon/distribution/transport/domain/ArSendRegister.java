package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: ArSendRegister
 * @Description: 发货登记表-实体类
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public class ArSendRegister extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 状态,1-已发货,2-已提取 */
	private Integer status;

	 /** 运输类型，1-航空，2-铁路 */
	private Integer transportType;

	 /** 航空单号/铁路单号 */
	private String orderCode;

	 /** 运力名称 */
	private String transportName;

	 /** 铁路站序 */
	private String siteOrder;

	 /** 发货日期 */
	private Date sendDate;

	 /** 航空公司 */
	private String airlineCompany;

	 /** 起飞城市 */
	private String startCityName;

	 /** 起飞城市编号 */
	private Integer startCityId;

	 /** 落地城市 */
	private String endCityName;

	 /** 落地城市编号 */
	private Integer endCityId;

	 /** 起飞机场 */
	private String startStationName;

	 /** 起飞机场编号 */
	private Integer startStationId;

	 /** 落地机场 */
	private String endStationName;

	 /** 落地机场编号 */
	private Integer endStationId;

	 /** 预计起飞时间 */
	private Date planStartTime;

	 /** 预计落地时间 */
	private Date planEndTime;

	 /** 发货件数 */
	private Integer sendNum;

	 /** 计费重量 */
	private BigDecimal chargedWeight;

	 /** 发货备注 */
	private String remark;

	 /** 摆渡车型 */
	private Integer shuttleBusType;

	 /** 摆渡车牌号 */
	private String shuttleBusNum;

	 /** 操作人ERP */
	private String operatorErp;

	 /** 操作部门 */
	private String operationDept;

	 /** 操作时间 */
	private Date operationTime;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;

	/**
	 * The set method for status.
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * The get method for status.
	 * @return this.status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 * The set method for transportType.
	 * @param transportType
	 */
	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}

	/**
	 * The get method for transportType.
	 * @return this.transportType
	 */
	public Integer getTransportType() {
		return this.transportType;
	}

	/**
	 * The set method for orderCode.
	 * @param orderCode
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * The get method for orderCode.
	 * @return this.orderCode
	 */
	public String getOrderCode() {
		return this.orderCode;
	}

	/**
	 * The set method for transportName.
	 * @param transportName
	 */
	public void setTransportName(String transportName) {
		this.transportName = transportName;
	}

	/**
	 * The get method for transportName.
	 * @return this.transportName
	 */
	public String getTransportName() {
		return this.transportName;
	}

	/**
	 * The set method for siteOrder.
	 * @param siteOrder
	 */
	public void setSiteOrder(String siteOrder) {
		this.siteOrder = siteOrder;
	}

	/**
	 * The get method for siteOrder.
	 * @return this.siteOrder
	 */
	public String getSiteOrder() {
		return this.siteOrder;
	}

	/**
	 * The set method for sendDate.
	 * @param sendDate
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * The get method for sendDate.
	 * @return this.sendDate
	 */
	public Date getSendDate() {
		return this.sendDate;
	}

	/**
	 * The set method for airlineCompany.
	 * @param airlineCompany
	 */
	public void setAirlineCompany(String airlineCompany) {
		this.airlineCompany = airlineCompany;
	}

	/**
	 * The get method for airlineCompany.
	 * @return this.airlineCompany
	 */
	public String getAirlineCompany() {
		return this.airlineCompany;
	}

	/**
	 * The set method for startCityName.
	 * @param startCityName
	 */
	public void setStartCityName(String startCityName) {
		this.startCityName = startCityName;
	}

	/**
	 * The get method for startCityName.
	 * @return this.startCityName
	 */
	public String getStartCityName() {
		return this.startCityName;
	}

	/**
	 * The set method for startCityId.
	 * @param startCityId
	 */
	public void setStartCityId(Integer startCityId) {
		this.startCityId = startCityId;
	}

	/**
	 * The get method for startCityId.
	 * @return this.startCityId
	 */
	public Integer getStartCityId() {
		return this.startCityId;
	}

	/**
	 * The set method for endCityName.
	 * @param endCityName
	 */
	public void setEndCityName(String endCityName) {
		this.endCityName = endCityName;
	}

	/**
	 * The get method for endCityName.
	 * @return this.endCityName
	 */
	public String getEndCityName() {
		return this.endCityName;
	}

	/**
	 * The set method for endCityId.
	 * @param endCityId
	 */
	public void setEndCityId(Integer endCityId) {
		this.endCityId = endCityId;
	}

	/**
	 * The get method for endCityId.
	 * @return this.endCityId
	 */
	public Integer getEndCityId() {
		return this.endCityId;
	}

	/**
	 * The set method for startStationName.
	 * @param startStationName
	 */
	public void setStartStationName(String startStationName) {
		this.startStationName = startStationName;
	}

	/**
	 * The get method for startStationName.
	 * @return this.startStationName
	 */
	public String getStartStationName() {
		return this.startStationName;
	}

	/**
	 * The set method for startStationId.
	 * @param startStationId
	 */
	public void setStartStationId(Integer startStationId) {
		this.startStationId = startStationId;
	}

	/**
	 * The get method for startStationId.
	 * @return this.startStationId
	 */
	public Integer getStartStationId() {
		return this.startStationId;
	}

	/**
	 * The set method for endStationName.
	 * @param endStationName
	 */
	public void setEndStationName(String endStationName) {
		this.endStationName = endStationName;
	}

	/**
	 * The get method for endStationName.
	 * @return this.endStationName
	 */
	public String getEndStationName() {
		return this.endStationName;
	}

	/**
	 * The set method for endStationId.
	 * @param endStationId
	 */
	public void setEndStationId(Integer endStationId) {
		this.endStationId = endStationId;
	}

	/**
	 * The get method for endStationId.
	 * @return this.endStationId
	 */
	public Integer getEndStationId() {
		return this.endStationId;
	}

	/**
	 * The set method for planStartTime.
	 * @param planStartTime
	 */
	public void setPlanStartTime(Date planStartTime) {
		this.planStartTime = planStartTime;
	}

	/**
	 * The get method for planStartTime.
	 * @return this.planStartTime
	 */
	public Date getPlanStartTime() {
		return this.planStartTime;
	}

	/**
	 * The set method for planEndTime.
	 * @param planEndTime
	 */
	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	/**
	 * The get method for planEndTime.
	 * @return this.planEndTime
	 */
	public Date getPlanEndTime() {
		return this.planEndTime;
	}

	/**
	 * The set method for sendNum.
	 * @param sendNum
	 */
	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	/**
	 * The get method for sendNum.
	 * @return this.sendNum
	 */
	public Integer getSendNum() {
		return this.sendNum;
	}

	/**
	 * The set method for chargedWeight.
	 * @param chargedWeight
	 */
	public void setChargedWeight(BigDecimal chargedWeight) {
		this.chargedWeight = chargedWeight;
	}

	/**
	 * The get method for chargedWeight.
	 * @return this.chargedWeight
	 */
	public BigDecimal getChargedWeight() {
		return this.chargedWeight;
	}

	/**
	 * The set method for remark.
	 * @param remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * The get method for remark.
	 * @return this.remark
	 */
	public String getRemark() {
		return this.remark;
	}

	/**
	 * The set method for shuttleBusType.
	 * @param shuttleBusType
	 */
	public void setShuttleBusType(Integer shuttleBusType) {
		this.shuttleBusType = shuttleBusType;
	}

	/**
	 * The get method for shuttleBusType.
	 * @return this.shuttleBusType
	 */
	public Integer getShuttleBusType() {
		return this.shuttleBusType;
	}

	/**
	 * The set method for shuttleBusNum.
	 * @param shuttleBusNum
	 */
	public void setShuttleBusNum(String shuttleBusNum) {
		this.shuttleBusNum = shuttleBusNum;
	}

	/**
	 * The get method for shuttleBusNum.
	 * @return this.shuttleBusNum
	 */
	public String getShuttleBusNum() {
		return this.shuttleBusNum;
	}

	/**
	 * The set method for operatorErp.
	 * @param operatorErp
	 */
	public void setOperatorErp(String operatorErp) {
		this.operatorErp = operatorErp;
	}

	/**
	 * The get method for operatorErp.
	 * @return this.operatorErp
	 */
	public String getOperatorErp() {
		return this.operatorErp;
	}

	/**
	 * The set method for operationDept.
	 * @param operationDept
	 */
	public void setOperationDept(String operationDept) {
		this.operationDept = operationDept;
	}

	/**
	 * The get method for operationDept.
	 * @return this.operationDept
	 */
	public String getOperationDept() {
		return this.operationDept;
	}

	/**
	 * The set method for operationTime.
	 * @param operationTime
	 */
	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

	/**
	 * The get method for operationTime.
	 * @return this.operationTime
	 */
	public Date getOperationTime() {
		return this.operationTime;
	}

	/**
	 * The set method for createUser.
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * The get method for createUser.
	 * @return this.createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 * The set method for updateUser.
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * The get method for updateUser.
	 * @return this.updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}


}
