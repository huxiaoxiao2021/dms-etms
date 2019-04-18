package com.jd.bluedragon.distribution.newseal.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: SealVehicles
 * @Description: 封车数据表-实体类
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public class SealVehicles extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 始发站点Code */
	private Integer createSiteCode;

	 /** 始发站点名称 */
	private String createSiteName;

	 /** 目的站点Code */
	private Integer receiveSiteCode;

	 /** 目的站点名称 */
	private String receiveSiteName;

	 /** 预封车UUID */
	private String preSealUuid;

	 /** 运力编码 */
	private String transportCode;

	 /** 任务码 */
	private String transWorkItemCode;

	 /** 封车编码 */
	private String sealCarCode;

	 /** 委托书编码 */
	private String transBookCode;

	 /** 封车业务数据 */
	private String sealDataCode;

	 /** 车牌号 */
	private String vehicleNumber;

	 /** 封签号多个逗号分割 */
	private String sealCodes;

	 /** 解封签号多个逗号分割 */
	private String dsealCodes;

	 /** 运力最晚发车时间 */
	private String sendCarTime;

	 /** 封车状态：1-封车，2-解封车 */
	private Integer status;

	 /** 封车类型：10-按运力 20-按派车任务明细 */
	private Integer sealCarType;

	 /** 重量 */
	private Double weight;

	 /** 体积 */
	private Double volume;

	 /** 封车人ERP */
	private String createUserErp;

	 /** 封车人name */
	private String createUserName;

	 /** 更新人ERP */
	private String updateUserErp;

	 /** 更新人name */
	private String updateUserName;

	 /** 系统标识 */
	private String source;

	 /** 预封车实操时间 */
	private Date operateTime;

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
	 * The set method for receiveSiteCode.
	 * @param receiveSiteCode
	 */
	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	/**
	 * The get method for receiveSiteCode.
	 * @return this.receiveSiteCode
	 */
	public Integer getReceiveSiteCode() {
		return this.receiveSiteCode;
	}

	/**
	 * The set method for receiveSiteName.
	 * @param receiveSiteName
	 */
	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	/**
	 * The get method for receiveSiteName.
	 * @return this.receiveSiteName
	 */
	public String getReceiveSiteName() {
		return this.receiveSiteName;
	}

	/**
	 * The set method for preSealUuid.
	 * @param preSealUuid
	 */
	public void setPreSealUuid(String preSealUuid) {
		this.preSealUuid = preSealUuid;
	}

	/**
	 * The get method for preSealUuid.
	 * @return this.preSealUuid
	 */
	public String getPreSealUuid() {
		return this.preSealUuid;
	}

	/**
	 * The set method for transportCode.
	 * @param transportCode
	 */
	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	/**
	 * The get method for transportCode.
	 * @return this.transportCode
	 */
	public String getTransportCode() {
		return this.transportCode;
	}

	/**
	 * The set method for transWorkItemCode.
	 * @param transWorkItemCode
	 */
	public void setTransWorkItemCode(String transWorkItemCode) {
		this.transWorkItemCode = transWorkItemCode;
	}

	/**
	 * The get method for transWorkItemCode.
	 * @return this.transWorkItemCode
	 */
	public String getTransWorkItemCode() {
		return this.transWorkItemCode;
	}

	/**
	 * The set method for sealCarCode.
	 * @param sealCarCode
	 */
	public void setSealCarCode(String sealCarCode) {
		this.sealCarCode = sealCarCode;
	}

	/**
	 * The get method for sealCarCode.
	 * @return this.sealCarCode
	 */
	public String getSealCarCode() {
		return this.sealCarCode;
	}

	/**
	 * The set method for transBookCode.
	 * @param transBookCode
	 */
	public void setTransBookCode(String transBookCode) {
		this.transBookCode = transBookCode;
	}

	/**
	 * The get method for transBookCode.
	 * @return this.transBookCode
	 */
	public String getTransBookCode() {
		return this.transBookCode;
	}

	/**
	 * The set method for sealDataCode.
	 * @param sealDataCode
	 */
	public void setSealDataCode(String sealDataCode) {
		this.sealDataCode = sealDataCode;
	}

	/**
	 * The get method for sealDataCode.
	 * @return this.sealDataCode
	 */
	public String getSealDataCode() {
		return this.sealDataCode;
	}

	/**
	 * The set method for vehicleNumber.
	 * @param vehicleNumber
	 */
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	/**
	 * The get method for vehicleNumber.
	 * @return this.vehicleNumber
	 */
	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	/**
	 * The set method for sealCodes.
	 * @param sealCodes
	 */
	public void setSealCodes(String sealCodes) {
		this.sealCodes = sealCodes;
	}

	/**
	 * The get method for sealCodes.
	 * @return this.sealCodes
	 */
	public String getSealCodes() {
		return this.sealCodes;
	}

	/**
	 * The set method for dsealCodes.
	 * @param dsealCodes
	 */
	public void setDsealCodes(String dsealCodes) {
		this.dsealCodes = dsealCodes;
	}

	/**
	 * The get method for dsealCodes.
	 * @return this.dsealCodes
	 */
	public String getDsealCodes() {
		return this.dsealCodes;
	}

	/**
	 * The set method for sendCarTime.
	 * @param sendCarTime
	 */
	public void setSendCarTime(String sendCarTime) {
		this.sendCarTime = sendCarTime;
	}

	/**
	 * The get method for sendCarTime.
	 * @return this.sendCarTime
	 */
	public String getSendCarTime() {
		return this.sendCarTime;
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
	 * The set method for sealCarType.
	 * @param sealCarType
	 */
	public void setSealCarType(Integer sealCarType) {
		this.sealCarType = sealCarType;
	}

	/**
	 * The get method for sealCarType.
	 * @return this.sealCarType
	 */
	public Integer getSealCarType() {
		return this.sealCarType;
	}

	/**
	 * The set method for weight.
	 * @param weight
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	/**
	 * The get method for weight.
	 * @return this.weight
	 */
	public Double getWeight() {
		return this.weight;
	}

	/**
	 * The set method for volume.
	 * @param volume
	 */
	public void setVolume(Double volume) {
		this.volume = volume;
	}

	/**
	 * The get method for volume.
	 * @return this.volume
	 */
	public Double getVolume() {
		return this.volume;
	}

	/**
	 * The set method for createUserErp.
	 * @param createUserErp
	 */
	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	/**
	 * The get method for createUserErp.
	 * @return this.createUserErp
	 */
	public String getCreateUserErp() {
		return this.createUserErp;
	}

	/**
	 * The set method for createUserName.
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 * The get method for createUserName.
	 * @return this.createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 * The set method for updateUserErp.
	 * @param updateUserErp
	 */
	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	/**
	 * The get method for updateUserErp.
	 * @return this.updateUserErp
	 */
	public String getUpdateUserErp() {
		return this.updateUserErp;
	}

	/**
	 * The set method for updateUserName.
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 * The get method for updateUserName.
	 * @return this.updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 * The set method for source.
	 * @param source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * The get method for source.
	 * @return this.source
	 */
	public String getSource() {
		return this.source;
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


}
