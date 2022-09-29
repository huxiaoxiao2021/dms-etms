package com.jd.bluedragon.distribution.jy.dto.seal;

import java.util.Date;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: JyAppDataSeal
 * @Description: 作业app-封车主页面数据表-实体类
 * @author wuyoude
 * @date 2022年09月27日 18:17:19
 *
 */
public class JyAppDataSeal implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * send_vehicle业务主键
	 */
	private String sendVehicleBizId;

	/**
	 * send_detail业务主键
	 */
	private String sendDetailBizId;

	/**
	 * 任务简码
	 */
	private String itemSimpleCode;

	/**
	 * 运力编码
	 */
	private String transportCode;

	/**
	 * 托盘
	 */
	private String palletCount;

	/**
	 * 重量
	 */
	private BigDecimal weight;

	/**
	 * 体积
	 */
	private BigDecimal volume;

	/**
	 * 创建人ERP
	 */
	private String createUserErp;

	/**
	 * 创建人姓名
	 */
	private String createUserName;

	/**
	 * 更新人ERP
	 */
	private String updateUserErp;

	/**
	 * 更新人姓名
	 */
	private String updateUserName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 是否删除：1-有效，0-删除
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 * @return id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 *
	 * @param sendVehicleBizId
	 */
	public void setSendVehicleBizId(String sendVehicleBizId) {
		this.sendVehicleBizId = sendVehicleBizId;
	}

	/**
	 *
	 * @return sendVehicleBizId
	 */
	public String getSendVehicleBizId() {
		return this.sendVehicleBizId;
	}

	/**
	 *
	 * @param sendDetailBizId
	 */
	public void setSendDetailBizId(String sendDetailBizId) {
		this.sendDetailBizId = sendDetailBizId;
	}

	/**
	 *
	 * @return sendDetailBizId
	 */
	public String getSendDetailBizId() {
		return this.sendDetailBizId;
	}

	/**
	 *
	 * @param itemSimpleCode
	 */
	public void setItemSimpleCode(String itemSimpleCode) {
		this.itemSimpleCode = itemSimpleCode;
	}

	/**
	 *
	 * @return itemSimpleCode
	 */
	public String getItemSimpleCode() {
		return this.itemSimpleCode;
	}

	/**
	 *
	 * @param transportCode
	 */
	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	/**
	 *
	 * @return transportCode
	 */
	public String getTransportCode() {
		return this.transportCode;
	}

	/**
	 *
	 * @param palletCount
	 */
	public void setPalletCount(String palletCount) {
		this.palletCount = palletCount;
	}

	/**
	 *
	 * @return palletCount
	 */
	public String getPalletCount() {
		return this.palletCount;
	}

	/**
	 *
	 * @param weight
	 */
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	/**
	 *
	 * @return weight
	 */
	public BigDecimal getWeight() {
		return this.weight;
	}

	/**
	 *
	 * @param volume
	 */
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	/**
	 *
	 * @return volume
	 */
	public BigDecimal getVolume() {
		return this.volume;
	}

	/**
	 *
	 * @param createUserErp
	 */
	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	/**
	 *
	 * @return createUserErp
	 */
	public String getCreateUserErp() {
		return this.createUserErp;
	}

	/**
	 *
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 *
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 *
	 * @param updateUserErp
	 */
	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	/**
	 *
	 * @return updateUserErp
	 */
	public String getUpdateUserErp() {
		return this.updateUserErp;
	}

	/**
	 *
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 *
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 *
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
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

	/**
	 *
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 *
	 * @return ts
	 */
	public Date getTs() {
		return this.ts;
	}


}
