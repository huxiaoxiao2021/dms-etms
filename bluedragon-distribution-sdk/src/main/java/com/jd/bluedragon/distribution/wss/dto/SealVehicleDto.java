package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaohengchong
 *
 * 封车对象
 */
public class SealVehicleDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 全局唯一ID */
    private Long id;

    /** 封车编号 */
    private String code;

    /** 车辆编号 */
    private String vehicleCode;

    /** 司机编号 */
    private String driverCode;

    /** 司机姓名 */
    private String driver;
    
    /**
     * 创建站点编号
     */
    private Integer createSiteCode;
    
    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;
    
    /**
     * 最后操作站点编号
     */
    private Integer receiveSiteCode;
    
    /** 最后操作人编号 */
    private Integer updateUserCode;

    /** 最后操作人 */
    private String updateUser;

    /** 最后修改时间 */
    private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVehicleCode() {
		return vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public String getDriverCode() {
		return driverCode;
	}

	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}
}
