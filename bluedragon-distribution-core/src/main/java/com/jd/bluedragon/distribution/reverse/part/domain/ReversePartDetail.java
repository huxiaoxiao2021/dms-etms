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

	 /** 总计包裹数 */
	private Integer allPackSum;

	/** 累计发货包裹数 */
	private Integer allSendPackSum;
	/** 未 退包裹数 */
	private Integer noSendPackSum;

	 /** 运单号  */
	private String waybillCode;

	 /** 操作单位编码 */
	private Integer createSiteCode;

	 /** 操作单位 */
	private String createSiteName;

	 /** 目的地编码 */
	private Integer receiveSiteCode;

	 /** 目的地 */
	private String receiveSiteName;

	 /** 半退类型 1-ECLP半退至仓 */
	private Integer type;

	 /** 状态 1-发货 2-取消发货 3-收货 4-拒收 */
	private Integer status;

	/** 包裹号 */
	private String packNo;

	/** 批次号 */
	private String sendCode;

	 /** 发货时间 */
	private Date sendTime;

	 /** 收货时间 */
	private Date receiveTime;

	 /** 创建人 */
	private String createUser;

	 /** 更新人 */
	private String updateUser;

	public Integer getAllPackSum() {
		return allPackSum;
	}

	public void setAllPackSum(Integer allPackSum) {
		this.allPackSum = allPackSum;
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


	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
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

	public String getPackNo() {
		return packNo;
	}

	public void setPackNo(String packNo) {
		this.packNo = packNo;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Integer getAllSendPackSum() {
		return allSendPackSum;
	}

	public void setAllSendPackSum(Integer allSendPackSum) {
		this.allSendPackSum = allSendPackSum;
	}

	public Integer getNoSendPackSum() {
		return noSendPackSum;
	}

	public void setNoSendPackSum(Integer noSendPackSum) {
		this.noSendPackSum = noSendPackSum;
	}
}
