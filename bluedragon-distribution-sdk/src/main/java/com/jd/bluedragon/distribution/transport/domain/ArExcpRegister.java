package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: ArExcpRegister
 * @Description: 异常登记表-实体类
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public class ArExcpRegister extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 异常日期 */
	private Date excpTime;
	private String excpTimeStr;
	 /** 异常节点 */
	private Integer excpNode;

	 /** 运输类型 */
	private Integer transportType;

	 /** 航空单号 */
	private String orderCode;

	 /** 运力名称 */
	private String transportName;

	 /** 铁路站序 */
	private String siteOrder;

	 /** 起飞城市 */
	private String startCityName;

	 /** 起飞城市编号 */
	private Integer startCityId;

	 /** 落地城市 */
	private String endCityName;

	 /** 落地城市编号 */
	private Integer endCityId;

	 /** 起飞时间 */
	 private Date planStartTime;
	private String planStartTimeStr;

	 /** 落地时间 */
	 private Date planEndTime;
	private String planEndTimeStr;

	 /** 异常类型 */
	private Integer excpType;

	 /** 异常类型名称 */
	private String excpTypeName;

	 /** 异常原因 */
	private Integer excpReason;

	 /** 异常原因名称 */
	private String excpReasonName;

	 /** 异常结果 */
	private Integer excpResult;

	 /** 异常结果名称 */
	private String excpResultName;

	 /** 发现异常城市 */
	private String excpCity;

	 /** 发现异常编号 */
	private Integer excpCityId;

	 /** 异常件数 */
	private Integer excpNum;

	 /** 异常包裹数 */
	private Integer excpPackageNum;

	 /** 现场操作人 */
	private String operatorErp;

	 /** 备注 */
	private String remark;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;
	/**发货批次号*/
	private String sendCode;

	/**
	 * The set method for excpTime.
	 * @param excpTime
	 */
	public void setExcpTime(Date excpTime) {
		this.excpTime = excpTime;
	}

	/**
	 * The get method for excpTime.
	 * @return this.excpTime
	 */
	public Date getExcpTime() {
		return this.excpTime;
	}

	/**
	 * The set method for excpNode.
	 * @param excpNode
	 */
	public void setExcpNode(Integer excpNode) {
		this.excpNode = excpNode;
	}

	/**
	 * The get method for excpNode.
	 * @return this.excpNode
	 */
	public Integer getExcpNode() {
		return this.excpNode;
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
	 * The set method for excpType.
	 * @param excpType
	 */
	public void setExcpType(Integer excpType) {
		this.excpType = excpType;
	}

	/**
	 * The get method for excpType.
	 * @return this.excpType
	 */
	public Integer getExcpType() {
		return this.excpType;
	}

	/**
	 * The set method for excpTypeName.
	 * @param excpTypeName
	 */
	public void setExcpTypeName(String excpTypeName) {
		this.excpTypeName = excpTypeName;
	}

	/**
	 * The get method for excpTypeName.
	 * @return this.excpTypeName
	 */
	public String getExcpTypeName() {
		return this.excpTypeName;
	}

	/**
	 * The set method for excpReason.
	 * @param excpReason
	 */
	public void setExcpReason(Integer excpReason) {
		this.excpReason = excpReason;
	}

	/**
	 * The get method for excpReason.
	 * @return this.excpReason
	 */
	public Integer getExcpReason() {
		return this.excpReason;
	}

	/**
	 * The set method for excpReasonName.
	 * @param excpReasonName
	 */
	public void setExcpReasonName(String excpReasonName) {
		this.excpReasonName = excpReasonName;
	}

	/**
	 * The get method for excpReasonName.
	 * @return this.excpReasonName
	 */
	public String getExcpReasonName() {
		return this.excpReasonName;
	}

	/**
	 * The set method for excpResult.
	 * @param excpResult
	 */
	public void setExcpResult(Integer excpResult) {
		this.excpResult = excpResult;
	}

	/**
	 * The get method for excpResult.
	 * @return this.excpResult
	 */
	public Integer getExcpResult() {
		return this.excpResult;
	}

	/**
	 * The set method for excpResultName.
	 * @param excpResultName
	 */
	public void setExcpResultName(String excpResultName) {
		this.excpResultName = excpResultName;
	}

	/**
	 * The get method for excpResultName.
	 * @return this.excpResultName
	 */
	public String getExcpResultName() {
		return this.excpResultName;
	}

	/**
	 * The set method for excpCity.
	 * @param excpCity
	 */
	public void setExcpCity(String excpCity) {
		this.excpCity = excpCity;
	}

	/**
	 * The get method for excpCity.
	 * @return this.excpCity
	 */
	public String getExcpCity() {
		return this.excpCity;
	}

	/**
	 * The set method for excpCityId.
	 * @param excpCityId
	 */
	public void setExcpCityId(Integer excpCityId) {
		this.excpCityId = excpCityId;
	}

	/**
	 * The get method for excpCityId.
	 * @return this.excpCityId
	 */
	public Integer getExcpCityId() {
		return this.excpCityId;
	}

	/**
	 * The set method for excpNum.
	 * @param excpNum
	 */
	public void setExcpNum(Integer excpNum) {
		this.excpNum = excpNum;
	}

	/**
	 * The get method for excpNum.
	 * @return this.excpNum
	 */
	public Integer getExcpNum() {
		return this.excpNum;
	}

	/**
	 * The set method for excpPackageNum.
	 * @param excpPackageNum
	 */
	public void setExcpPackageNum(Integer excpPackageNum) {
		this.excpPackageNum = excpPackageNum;
	}

	/**
	 * The get method for excpPackageNum.
	 * @return this.excpPackageNum
	 */
	public Integer getExcpPackageNum() {
		return this.excpPackageNum;
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


	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getExcpTimeStr() {
		return excpTimeStr;
	}

	public void setExcpTimeStr(String excpTimeStr) {
		this.excpTimeStr = excpTimeStr;
	}

	public String getPlanStartTimeStr() {
		return planStartTimeStr;
	}

	public void setPlanStartTimeStr(String planStartTimeStr) {
		this.planStartTimeStr = planStartTimeStr;
	}

	public String getPlanEndTimeStr() {
		return planEndTimeStr;
	}

	public void setPlanEndTimeStr(String planEndTimeStr) {
		this.planEndTimeStr = planEndTimeStr;
	}
}
