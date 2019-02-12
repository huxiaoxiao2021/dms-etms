package com.jd.bluedragon.distribution.reverse.part.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: ReversePartDetail
 * @Description: 半退明细表-实体类
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
public class ReversePartDetail extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 累计发货包裹数 */
	private Integer allSendPackSum;

	 /** 本次发货包裹数 */
	private Integer sendPackSum;

	 /** 未退包裹数 */
	private Integer noSendPackSum;

	 /** 运单号  */
	private String waybillCode;

	 /** 操作单位编码 */
	private Integer createSiteCode;

	 /** 操作单位 */
	private String createSiteName;

	 /** 目的地编码 */
	private Integer reverseSiteCode;

	 /** 目的地 */
	private String reverseSiteName;

	 /** 半退类型 1-ECLP半退至仓 */
	private Integer type;

	 /** 状态 1-发货 2-取消发货 3-收货 4-拒收 */
	private Integer status;

	 /** 发货时间 */
	private Date sendTime;

	 /** 收货时间 */
	private Date receiveTime;

	 /** 创建人 */
	private String createUser;

	 /** 更新人 */
	private String updateUser;

	/**
	 * The set method for allSendPackSum.
	 * @param allSendPackSum
	 */
	public void setAllSendPackSum(Integer allSendPackSum) {
		this.allSendPackSum = allSendPackSum;
	}

	/**
	 * The get method for allSendPackSum.
	 * @return this.allSendPackSum
	 */
	public Integer getAllSendPackSum() {
		return this.allSendPackSum;
	}

	/**
	 * The set method for sendPackSum.
	 * @param sendPackSum
	 */
	public void setSendPackSum(Integer sendPackSum) {
		this.sendPackSum = sendPackSum;
	}

	/**
	 * The get method for sendPackSum.
	 * @return this.sendPackSum
	 */
	public Integer getSendPackSum() {
		return this.sendPackSum;
	}

	/**
	 * The set method for noSendPackSum.
	 * @param noSendPackSum
	 */
	public void setNoSendPackSum(Integer noSendPackSum) {
		this.noSendPackSum = noSendPackSum;
	}

	/**
	 * The get method for noSendPackSum.
	 * @return this.noSendPackSum
	 */
	public Integer getNoSendPackSum() {
		return this.noSendPackSum;
	}

	/**
	 * The set method for waybillCode.
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * The get method for waybillCode.
	 * @return this.waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 * The set method for createSiteCode.
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * The get method for createSiteCode.
	 * @return this.createSiteCode
	 */
	public Integer getCreateSiteCode() {
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
	 * The set method for reverseSiteCode.
	 * @param reverseSiteCode
	 */
	public void setReverseSiteCode(Integer reverseSiteCode) {
		this.reverseSiteCode = reverseSiteCode;
	}

	/**
	 * The get method for reverseSiteCode.
	 * @return this.reverseSiteCode
	 */
	public Integer getReverseSiteCode() {
		return this.reverseSiteCode;
	}

	/**
	 * The set method for reverseSiteName.
	 * @param reverseSiteName
	 */
	public void setReverseSiteName(String reverseSiteName) {
		this.reverseSiteName = reverseSiteName;
	}

	/**
	 * The get method for reverseSiteName.
	 * @return this.reverseSiteName
	 */
	public String getReverseSiteName() {
		return this.reverseSiteName;
	}

	/**
	 * The set method for type.
	 * @param type
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * The get method for type.
	 * @return this.type
	 */
	public Integer getType() {
		return this.type;
	}

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
	 * The set method for sendTime.
	 * @param sendTime
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * The get method for sendTime.
	 * @return this.sendTime
	 */
	public Date getSendTime() {
		return this.sendTime;
	}

	/**
	 * The set method for receiveTime.
	 * @param receiveTime
	 */
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	/**
	 * The get method for receiveTime.
	 * @return this.receiveTime
	 */
	public Date getReceiveTime() {
		return this.receiveTime;
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
