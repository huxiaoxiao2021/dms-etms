package com.jd.bluedragon.distribution.transport.domain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;


/**
 *
 * @ClassName: ArBookingSpace
 * @Description: 空铁项目-订舱表-实体类
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public class ArBookingSpace extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 预计起飞时间（根据运力类型描述可代表不同含义） */
	 private Date planStartDate;
	private String planStartDateStr;
	 /** 分拣中心编号 */
	private Long createSiteCode;

	 /** 分拣中心名称 */
	private String createSiteName;

	 /** 运力名称(实际含义航班号/铁路车次号) */
	private String transportName;

	 /** 运力类型(1-散航，2-全货机，3-铁路) */
	private Integer transportType;
	/** Excel 传入的是汉字，暂时存在该字段中 用的时候转换一下*/
	private String transportTypeForExcel;
	 /** 起飞城市编号 */
	private Integer startCityId;

	 /** 起飞城市（对于航班则是起飞城市，对于铁路则是装货发车城市） */
	private String startCityName;

	 /** 落地城市编号 */
	private Integer endCityId;

	 /** 落地城市(对于航班则是落地城市，对于铁路则是卸货城市) */
	private String endCityName;

	 /** 预计起飞时间（根据不同的运力类型可有不同含义） */
	private Date planStartTime;
	private String planStartTimeStr;

	 /** 预计落地时间（根据不同的运力类型可有不同含义） */
	 private Date planEndTime;
	private String planEndTimeStr;

	/** 优先级 */
	private String priority;

	 /** 可获取舱位（单位：kg） */
	private BigDecimal gainSpace;

	 /** 计划订舱位（单位：kg） */
	private BigDecimal planSpace;

	 /** 实际订舱位（单位：kg） */
	private BigDecimal realSpace;

	 /** 订舱日期 */
	 private Date bookingSpaceTime;
	private String bookingSpaceTimeStr;
	 /** 供应商名称 */
	private String supplierName;

	 /** 联系电话 */
	private String phone;

	 /** 备注 */
	private String remark;

	 /** 操作人ERP */
	private String operatorErp;

	 /** 操作人姓名 */
	private String operatorName;

	 /** 操作时间 */
	private Date operateTime;

	 /** 创建人 */
	private String createUser;

	 /** 更新人 */
	private String updateUser;

	/**
	 * The set method for planStartDate.
	 * @param planStartDate
	 */
	public void setPlanStartDate(Date planStartDate) {
		this.planStartDate = planStartDate;
	}

	/**
	 * The get method for planStartDate.
	 * @return this.planStartDate
	 */
	public Date getPlanStartDate() {
		return this.planStartDate;
	}

	/**
	 * The set method for createSiteCode.
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Long createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * The get method for createSiteCode.
	 * @return this.createSiteCode
	 */
	public Long getCreateSiteCode() {
		return this.createSiteCode;
	}

	/**
	 * The set method for createSiteName.
	 * @param createSiteName
	 */
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	/**
	 * The get method for createSiteName.
	 * @return this.createSiteName
	 */
	public String getCreateSiteName() {
		return this.createSiteName;
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
	 * The set method for priority.
	 * @param priority
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * The get method for priority.
	 * @return this.priority
	 */
	public String getPriority() {
		return this.priority;
	}

	/**
	 * The set method for gainSpace.
	 * @param gainSpace
	 */
	public void setGainSpace(BigDecimal gainSpace) {
		this.gainSpace = gainSpace;
	}

	/**
	 * The get method for gainSpace.
	 * @return this.gainSpace
	 */
	public BigDecimal getGainSpace() {
		return this.gainSpace;
	}

	/**
	 * The set method for planSpace.
	 * @param planSpace
	 */
	public void setPlanSpace(BigDecimal planSpace) {
		this.planSpace = planSpace;
	}

	/**
	 * The get method for planSpace.
	 * @return this.planSpace
	 */
	public BigDecimal getPlanSpace() {
		return this.planSpace;
	}

	/**
	 * The set method for realSpace.
	 * @param realSpace
	 */
	public void setRealSpace(BigDecimal realSpace) {
		this.realSpace = realSpace;
	}

	/**
	 * The get method for realSpace.
	 * @return this.realSpace
	 */
	public BigDecimal getRealSpace() {
		return this.realSpace;
	}

	/**
	 * The set method for bookingSpaceTime.
	 * @param bookingSpaceTime
	 */
	public void setBookingSpaceTime(Date bookingSpaceTime) {
		this.bookingSpaceTime = bookingSpaceTime;
	}

	/**
	 * The get method for bookingSpaceTime.
	 * @return this.bookingSpaceTime
	 */
	public Date getBookingSpaceTime() {
		return this.bookingSpaceTime;
	}

	/**
	 * The set method for supplierName.
	 * @param supplierName
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * The get method for supplierName.
	 * @return this.supplierName
	 */
	public String getSupplierName() {
		return this.supplierName;
	}

	/**
	 * The set method for phone.
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * The get method for phone.
	 * @return this.phone
	 */
	public String getPhone() {
		return this.phone;
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
	 * The set method for operatorName.
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * The get method for operatorName.
	 * @return this.operatorName
	 */
	public String getOperatorName() {
		return this.operatorName;
	}

	/**
	 * The set method for operateTime.
	 * @param operateTime
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 * The get method for operateTime.
	 * @return this.operateTime
	 */
	public Date getOperateTime() {
		return this.operateTime;
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

	public String getTransportTypeForExcel() {
		return transportTypeForExcel;
	}

	public void setTransportTypeForExcel(String transportTypeForExcel) {
		this.transportTypeForExcel = transportTypeForExcel;
	}

	public String getPlanStartDateStr() {
		return planStartDateStr;
	}

	public void setPlanStartDateStr(String planStartDateStr) {
		this.planStartDateStr = planStartDateStr;
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

	public String getBookingSpaceTimeStr() {
		return bookingSpaceTimeStr;
	}

	public void setBookingSpaceTimeStr(String bookingSpaceTimeStr) {
		this.bookingSpaceTimeStr = bookingSpaceTimeStr;
	}
}
