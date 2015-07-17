package com.jd.bluedragon.distribution.inspection.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.utils.DateHelper;

/**
 * 验货异常比对表 db:db_dms
 *
 * @author wangzichen
 *
 */
public class InspectionEC implements java.io.Serializable, Comparable<InspectionEC> {

	private static final long serialVersionUID = -1945134711099373046L;

	/* 验货异常比对表主键 */
	private Long checkId;

	/* 运单号 */
	private String waybillCode;

	/* 箱号 */
	private String boxCode;

	/* 包裹号 */
	private String packageBarcode;

	/* 验货异常类型(1:少验,2:多验) */
	private Integer inspectionECType;

	/* 状态0:未处理 1:已处理 */
	private Integer status;

	/* 验货类型 */
	private Integer inspectionType;

	/* 异常类型(用于退货) */
	private String exceptionType;

	/* 创建人name */
	private String createUser;

	/* 创建人code */
	private Integer createUserCode;

	/* 创建时间 */
	private Date createTime;

	/* 创建站点Code */
	private Integer createSiteCode;

	/* 收货单位Code */
	private Integer receiveSiteCode;

	/* 最后更新人name */
	private String updateUser;

	/* 最后更新人code */
	private Integer updateUserCode;

	/* 最后更新时间 */
	private Date updateTime;

	private Integer yn;

	private Integer startNo;

	private Integer limitNo;

	private String inspectionECTypes;

	private Integer operationType;

	public static final int INSPECTION_EXCEPTION_STATUS_UNHANDLED = 0;
	public static final int INSPECTION_EXCEPTION_STATUS_HANDLED = 1;

	/* 正常 */
	public static final int INSPECTIONEC_TYPE_NORMAL = 0;
	/* 少验 */
	public static final int INSPECTIONEC_TYPE_LESS = 1;
	/* 多验 */
	public static final int INSPECTIONEC_TYPE_MORE = 2;

	/* 超区退回 */
	public static final int INSPECTIONEC_TYPE_OVER = 3;
	/* 多验退回 */
	public static final int INSPECTIONEC_TYPE_SEND_BACK = 4;
	/* 少验取消 */
	public static final int INSPECTIONEC_TYPE_CANCEL = 5;
	/* 多验直接配送 */
	public static final int INSPECTIONEC_TYPE_SEND = 6;

	/* 取消分拣 */
	public static final int INSPECTIONEC_TYPE_CANCEL_SORTING = 7;

	public InspectionEC() {
		super();
	}

	public InspectionEC(String boxCode, String packageBarcode, Integer createSiteCode,
	        Integer receiveSiteCode) {
		super();
		this.boxCode = boxCode;
		this.packageBarcode = packageBarcode;
		this.createSiteCode = createSiteCode;
		this.receiveSiteCode = receiveSiteCode;
	}

	public Long getCheckId() {
		return checkId;
	}

	public void setCheckId(Long checkId) {
		this.checkId = checkId;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public Integer getInspectionECType() {
		return inspectionECType;
	}

	public void setInspectionECType(Integer inspectionECType) {
		this.inspectionECType = inspectionECType;
	}

	public Integer getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(Integer inspectionType) {
		this.inspectionType = inspectionType;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Date getCreateTime() {
		return createTime != null ? (Date) createTime.clone() : null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime != null ? (Date) createTime.clone() : null;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Integer getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public Date getUpdateTime() {
		return updateTime != null ? (Date) updateTime.clone() : null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime != null ? (Date) updateTime.clone() : null;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Integer getStartNo() {
		return startNo;
	}

	public void setStartNo(Integer startNo) {
		this.startNo = startNo;
	}

	public Integer getLimitNo() {
		return limitNo;
	}

	public void setLimitNo(Integer limitNo) {
		this.limitNo = limitNo;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getInspectionECTypes() {
		return inspectionECTypes;
	}

	public void setInspectionECTypes(String inspectionECTypes) {
		this.inspectionECTypes = inspectionECTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (packageBarcode == null ? 0 : packageBarcode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InspectionEC other = (InspectionEC) obj;
		if (packageBarcode == null) {
			if (other.packageBarcode != null) {
				return false;
			}
		} else if (!packageBarcode.equals(other.packageBarcode)) {
			return false;
		}
		return true;
	}

	public static InspectionEC toInspectionEC(InspectionRequest requestBean) {
		if (null == requestBean) {
			return null;
		}

		InspectionEC inspectionEC = new InspectionEC();
		inspectionEC.setWaybillCode(requestBean.getWaybillCode());
		inspectionEC.setPackageBarcode(requestBean.getPackageBarcode());
		Date date = requestBean.getOperateTime() == null ? new Date() : DateHelper
		        .getSeverTime(requestBean.getOperateTime());
		inspectionEC.setUpdateTime(date);
		return inspectionEC;
	}

	public static InspectionEC toInspectionECByInspection(Inspection inspection) {
		if (null == inspection) {
			return null;
		}
		InspectionEC inspectionEC = new InspectionEC();
		inspectionEC.setBoxCode(inspection.getBoxCode());
		inspectionEC.setCreateSiteCode(inspection.getCreateSiteCode());
		inspectionEC.setCreateUser(inspection.getCreateUser());
		inspectionEC.setCreateUserCode(inspection.getCreateUserCode());
		inspectionEC.setExceptionType(inspection.getExceptionType());
		inspectionEC.setInspectionType(inspection.getInspectionType());
		inspectionEC.setPackageBarcode(inspection.getPackageBarcode());
		inspectionEC.setReceiveSiteCode(inspection.getReceiveSiteCode());
		inspectionEC.setUpdateUser(inspection.getUpdateUser());
		inspectionEC.setUpdateUserCode(inspection.getUpdateUserCode());
		inspectionEC.setWaybillCode(inspection.getWaybillCode());
		inspectionEC.setCreateTime(inspection.getCreateTime());
		inspectionEC.setUpdateTime(inspection.getUpdateTime());
		return inspectionEC;
	}

	public static class Builder {

		/* Required parameters */
		/* 包裹号 */
		private String packageBarcode;

		/* 操作单位Code */
		private Integer createSiteCode;

		/* Optional parameters */
		/* 验货异常比对表主键 */
		private Long checkId;

		/* 运单号 */
		private String waybillCode;

		/* 箱号 */
		private String boxCode;

		/* 验货异常类型(1:少验,2:多验) */
		private Integer inspectionECType;

		/* 状态0:未处理 1:已处理 */
		private Integer status;

		/* 验货类型 */
		private Integer inspectionType;

		/* 创建人name */
		private String createUser;

		/* 创建人code */
		private Integer createUserCode;

		/* 创建时间 */
		private Date createTime;

		/* 收货单位Code */
		private Integer receiveSiteCode;

		/* 最后更新人name */
		private String updateUser;

		/* 最后更新人code */
		private Integer updateUserCode;

		/* 最后更新时间 */
		private Date updateTime;

		private Integer yn;

		public Builder(String packageBarcode, Integer createSiteCode) {
			super();
			this.packageBarcode = packageBarcode;
			this.createSiteCode = createSiteCode;
		}

		public Builder checkId(Long val) {
			checkId = val;
			return this;
		}

		public Builder createUser(String val) {
			createUser = val;
			return this;
		}

		public Builder createUserCode(Integer val) {
			createUserCode = val;
			return this;
		}

		public Builder receiveSiteCode(Integer val) {
			receiveSiteCode = val;
			return this;
		}

		public Builder boxCode(String val) {
			boxCode = val;
			return this;
		}

		public Builder createTime(Date val) {
			createTime = val != null ? (Date) val.clone() : null;
			return this;
		}

		public Builder updateTime(Date val) {
			updateTime = val != null ? (Date) val.clone() : null;
			return this;
		}

		public Builder inspectionECType(Integer val) {
			inspectionECType = val;
			return this;
		}

		public Builder status(Integer val) {
			status = val;
			return this;
		}

		public Builder updateUser(String val) {
			updateUser = val;
			return this;
		}

		public Builder updateUserCode(Integer val) {
			updateUserCode = val;
			return this;
		}

		public Builder waybillCode(String val) {
			waybillCode = val;
			return this;
		}

		public Builder inspectionType(Integer val) {
			inspectionType = val;
			return this;
		}

		public Builder yn(Integer val) {
			yn = val;
			return this;
		}

		public InspectionEC build() {
			return new InspectionEC(this);
		}
	}

	public InspectionEC(Builder builder) {
		this.packageBarcode = builder.packageBarcode;
		this.createSiteCode = builder.createSiteCode;
		this.checkId = builder.checkId;
		this.boxCode = builder.boxCode;
		this.receiveSiteCode = builder.receiveSiteCode;
		this.createTime = builder.createTime;
		this.updateTime = builder.updateTime;
		this.inspectionECType = builder.inspectionECType;
		this.status = builder.status;
		this.createUser = builder.createUser;
		this.createUserCode = builder.createUserCode;
		this.updateUser = builder.updateUser;
		this.updateUserCode = builder.updateUserCode;
		this.waybillCode = builder.waybillCode;
		this.inspectionType = builder.inspectionType;
		this.yn = builder.yn;
	}

	@Override
	public int compareTo(InspectionEC inspectionEC) {
		if (null == inspectionEC || StringUtils.isBlank(inspectionEC.getPackageBarcode())
		        || StringUtils.isBlank(this.getPackageBarcode())) {
			return 0;
		} else {
			return this.getPackageBarcode().compareTo(inspectionEC.getPackageBarcode());
		}
	}
}