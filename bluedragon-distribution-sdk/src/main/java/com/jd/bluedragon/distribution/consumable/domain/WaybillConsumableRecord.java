package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: WaybillConsumableRecord
 * @Description: 运单耗材记录表-实体类
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public class WaybillConsumableRecord extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 分拣中心编号 */
	private Integer dmsId;

	 /** 分拣中心名称 */
	private String dmsName;

	 /** 运单号 */
	private String waybillCode;

	 /** 确认状态（0：未确认 1：已确认） */
	private Integer confirmStatus;

	 /** 修改状态（0：未修改 1：已修改） */
	private Integer modifyStatus;

	 /** 揽收人编号 */
	private String receiveUserCode;

	 /** 揽收人erp */
	private String receiveUserErp;

	 /**  */
	private String receiveUserName;

	 /** 确认人编号 */
	private String confirmUserCode;

	 /** 确认人erp */
	private String confirmUserErp;

	 /** 揽收时间 */
	private Date receiveTime;

	 /** 操作时间 */
	private Date confirmTime;

	/**
	 * The set method for dmsId.
	 * @param dmsId
	 */
	public void setDmsId(Integer dmsId) {
		this.dmsId = dmsId;
	}

	/**
	 * The get method for dmsId.
	 * @return this.dmsId
	 */
	public Integer getDmsId() {
		return this.dmsId;
	}

	/**
	 * The set method for dmsName.
	 * @param dmsName
	 */
	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	/**
	 * The get method for dmsName.
	 * @return this.dmsName
	 */
	public String getDmsName() {
		return this.dmsName;
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
	 * The set method for confirmStatus.
	 * @param confirmStatus
	 */
	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	/**
	 * The get method for confirmStatus.
	 * @return this.confirmStatus
	 */
	public Integer getConfirmStatus() {
		return this.confirmStatus;
	}

	/**
	 * The set method for modifyStatus.
	 * @param modifyStatus
	 */
	public void setModifyStatus(Integer modifyStatus) {
		this.modifyStatus = modifyStatus;
	}

	/**
	 * The get method for modifyStatus.
	 * @return this.modifyStatus
	 */
	public Integer getModifyStatus() {
		return this.modifyStatus;
	}

	/**
	 * The set method for receiveUserCode.
	 * @param receiveUserCode
	 */
	public void setReceiveUserCode(String receiveUserCode) {
		this.receiveUserCode = receiveUserCode;
	}

	/**
	 * The get method for receiveUserCode.
	 * @return this.receiveUserCode
	 */
	public String getReceiveUserCode() {
		return this.receiveUserCode;
	}

	/**
	 * The set method for receiveUserErp.
	 * @param receiveUserErp
	 */
	public void setReceiveUserErp(String receiveUserErp) {
		this.receiveUserErp = receiveUserErp;
	}

	/**
	 * The get method for receiveUserErp.
	 * @return this.receiveUserErp
	 */
	public String getReceiveUserErp() {
		return this.receiveUserErp;
	}

	/**
	 * The set method for receiveUserName.
	 * @param receiveUserName
	 */
	public void setReceiveUserName(String receiveUserName) {
		this.receiveUserName = receiveUserName;
	}

	/**
	 * The get method for receiveUserName.
	 * @return this.receiveUserName
	 */
	public String getReceiveUserName() {
		return this.receiveUserName;
	}

	/**
	 * The set method for confirmUserCode.
	 * @param confirmUserCode
	 */
	public void setConfirmUserCode(String confirmUserCode) {
		this.confirmUserCode = confirmUserCode;
	}

	/**
	 * The get method for confirmUserCode.
	 * @return this.confirmUserCode
	 */
	public String getConfirmUserCode() {
		return this.confirmUserCode;
	}

	/**
	 * The set method for confirmUserErp.
	 * @param confirmUserErp
	 */
	public void setConfirmUserErp(String confirmUserErp) {
		this.confirmUserErp = confirmUserErp;
	}

	/**
	 * The get method for confirmUserErp.
	 * @return this.confirmUserErp
	 */
	public String getConfirmUserErp() {
		return this.confirmUserErp;
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
	 * The set method for confirmTime.
	 * @param confirmTime
	 */
	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	/**
	 * The get method for confirmTime.
	 * @return this.confirmTime
	 */
	public Date getConfirmTime() {
		return this.confirmTime;
	}


}
