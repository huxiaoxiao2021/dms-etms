package com.jd.bluedragon.distribution.business.entity;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @ClassName: BusinessReturnAdress
 * @Description: 商家退货地址信息-实体类
 * @author wuyoude
 * @date 2020年07月28日 16:45:14
 *
 */
public class BusinessReturnAdress extends DbEntity {

	private static final long serialVersionUID = 1L;
	/**
	 * 序号
	 */
	private Integer rowNum;
	/**
	 * 分拣中心编码
	 */
	private Integer dmsSiteCode;

	/**
	 * 分拣中心名称
	 */
	private String dmsSiteName;

	/**
	 * 商家id
	 */
	private Integer businessId;
	/**
	 * 商家编码
	 */
	private String businessCode;
	/**
	 * 商家名称
	 */
	private String businessName;

    /**
     * 事业部编码
     * */
    private String deptNo;

	/**
	 * 上次通知时间
	 */
	private Date lastNoticeTime;

	/**
	 * 上次操作换单时间
	 */
	private Date lastOperateTime;

	/**
	 * 退货地址维护状态-0-未维护 1-已维护
	 */
	private Integer returnAdressStatus;
	/**
	 * 退货地址维护状态-0-未维护 1-已维护
	 */
	private String returnAdressStatusDesc;

	/**
     * 退货量
     * */
	private Integer returnQuantity;

	/**
	 * 1-有效，0-无效
	 */
	private Integer yn;

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	/**
	 *
	 * @param dmsSiteCode
	 */
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}

	/**
	 *
	 * @return dmsSiteCode
	 */
	public Integer getDmsSiteCode() {
		return this.dmsSiteCode;
	}

	/**
	 *
	 * @param dmsSiteName
	 */
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}

	/**
	 *
	 * @return dmsSiteName
	 */
	public String getDmsSiteName() {
		return this.dmsSiteName;
	}

	/**
	 *
	 * @param businessId
	 */
	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}

	/**
	 *
	 * @return businessId
	 */
	public Integer getBusinessId() {
		return this.businessId;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	/**
	 *
	 * @param businessName
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	/**
	 *
	 * @return businessName
	 */
	public String getBusinessName() {
		return this.businessName;
	}

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    /**
	 *
	 * @param lastNoticeTime
	 */
	public void setLastNoticeTime(Date lastNoticeTime) {
		this.lastNoticeTime = lastNoticeTime;
	}

	/**
	 *
	 * @return lastNoticeTime
	 */
	public Date getLastNoticeTime() {
		return this.lastNoticeTime;
	}

	/**
	 *
	 * @param lastOperateTime
	 */
	public void setLastOperateTime(Date lastOperateTime) {
		this.lastOperateTime = lastOperateTime;
	}

	/**
	 *
	 * @return lastOperateTime
	 */
	public Date getLastOperateTime() {
		return this.lastOperateTime;
	}

	/**
	 *
	 * @param returnAdressStatus
	 */
	public void setReturnAdressStatus(Integer returnAdressStatus) {
		this.returnAdressStatus = returnAdressStatus;
	}

	/**
	 *
	 * @return returnAdressStatus
	 */
	public Integer getReturnAdressStatus() {
		return this.returnAdressStatus;
	}

	public String getReturnAdressStatusDesc() {
		return returnAdressStatusDesc;
	}

	public void setReturnAdressStatusDesc(String returnAdressStatusDesc) {
		this.returnAdressStatusDesc = returnAdressStatusDesc;
	}

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    /**
	 *
	 * @param yn
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Integer getYn() {
		return this.yn;
	}


}
